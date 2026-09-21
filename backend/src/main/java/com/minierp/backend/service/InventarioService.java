package com.minierp.backend.service;

import com.minierp.backend.dto.inventario.LoteResponse;
import com.minierp.backend.dto.inventario.MovimientoResponse;
import com.minierp.backend.entity.DetalleCompra;
import com.minierp.backend.entity.Lote;
import com.minierp.backend.entity.MovimientoInventario;
import com.minierp.backend.entity.Producto;
import com.minierp.backend.entity.Usuario;
import com.minierp.backend.exception.NegocioException;
import com.minierp.backend.exception.NoEncontradoException;
import com.minierp.backend.repository.LoteRepository;
import com.minierp.backend.repository.MovimientoInventarioRepository;
import com.minierp.backend.repository.ProductoRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Unico lugar donde cambia la existencia de un producto.
// Toda entrada crea un lote y toda salida consume lotes en orden PEPS,
// y ambas dejan un movimiento que explica el cambio.
@Service
public class InventarioService {

    private final ProductoRepository productoRepository;
    private final LoteRepository loteRepository;
    private final MovimientoInventarioRepository movimientoRepository;
    private final EntityManager entityManager;

    public InventarioService(ProductoRepository productoRepository,
                             LoteRepository loteRepository,
                             MovimientoInventarioRepository movimientoRepository,
                             EntityManager entityManager) {
        this.productoRepository = productoRepository;
        this.loteRepository = loteRepository;
        this.movimientoRepository = movimientoRepository;
        this.entityManager = entityManager;
    }

    // Entrada por compra: crea un lote nuevo con su propio costo y suma a la existencia
    @Transactional
    public Lote registrarEntrada(Integer idProducto, int cantidad, BigDecimal costoUnitario,
                                 DetalleCompra detalleCompra, Integer idCompra, Usuario usuario) {
        if (cantidad <= 0) {
            throw new NegocioException("La cantidad de entrada debe ser mayor a 0");
        }
        Producto producto = bloquearProducto(idProducto);

        Lote lote = new Lote();
        lote.setProducto(producto);
        lote.setDetalleCompra(detalleCompra);
        lote.setCantidadInicial(cantidad);
        lote.setCantidadDisponible(cantidad);
        lote.setCostoUnitario(costoUnitario);
        lote.setFechaIngreso(LocalDateTime.now());
        loteRepository.save(lote);

        registrarMovimiento(producto, MovimientoInventario.ENTRADA, cantidad, costoUnitario,
                MovimientoInventario.ORIGEN_COMPRA, idCompra, usuario);
        return lote;
    }

    // Salida por venta con PEPS: consume primero los lotes mas antiguos.
    // Devuelve el costo total real de las unidades que salieron.
    @Transactional
    public BigDecimal registrarSalidaPeps(Integer idProducto, int cantidad, Integer idVenta, Usuario usuario) {
        if (cantidad <= 0) {
            throw new NegocioException("La cantidad de salida debe ser mayor a 0");
        }
        Producto producto = bloquearProducto(idProducto);

        if (producto.getExistenciaActual() < cantidad) {
            throw new NegocioException("Stock insuficiente para " + producto.getNombre()
                    + ": disponible " + producto.getExistenciaActual() + ", solicitado " + cantidad);
        }

        List<Lote> lotes = loteRepository.findDisponiblesPeps(idProducto);
        int pendiente = cantidad;
        BigDecimal costoTotal = BigDecimal.ZERO;

        for (Lote lote : lotes) {
            if (pendiente == 0) {
                break;
            }
            int tomar = Math.min(pendiente, lote.getCantidadDisponible());
            lote.setCantidadDisponible(lote.getCantidadDisponible() - tomar);
            pendiente -= tomar;
            costoTotal = costoTotal.add(lote.getCostoUnitario().multiply(BigDecimal.valueOf(tomar)));

            // Un movimiento por cada lote consumido, con el costo real de ese lote
            registrarMovimiento(producto, MovimientoInventario.SALIDA, tomar, lote.getCostoUnitario(),
                    MovimientoInventario.ORIGEN_VENTA, idVenta, usuario);
        }

        if (pendiente > 0) {
            throw new NegocioException("Los lotes de " + producto.getNombre()
                    + " no cubren la existencia registrada");
        }
        return costoTotal;
    }

    @Transactional(readOnly = true)
    public List<MovimientoResponse> historial(Integer idProducto) {
        validarProducto(idProducto);
        return movimientoRepository.findByProductoIdProductoOrderByFechaAscIdMovimientoAsc(idProducto)
                .stream().map(MovimientoResponse::from).toList();
    }

    // Todas las capas del producto, en el orden en que PEPS las consume
    @Transactional(readOnly = true)
    public List<LoteResponse> lotes(Integer idProducto) {
        validarProducto(idProducto);
        return entityManager.createQuery(
                        "SELECT l FROM Lote l WHERE l.producto.idProducto = :id "
                                + "ORDER BY l.fechaIngreso ASC, l.idLote ASC", Lote.class)
                .setParameter("id", idProducto)
                .getResultList()
                .stream().map(LoteResponse::from).toList();
    }

    private void registrarMovimiento(Producto producto, String tipo, int cantidad, BigDecimal costo,
                                     String origenTipo, Integer origenId, Usuario usuario) {
        int anterior = producto.getExistenciaActual();
        int nueva = MovimientoInventario.ENTRADA.equals(tipo) ? anterior + cantidad : anterior - cantidad;
        if (nueva < 0) {
            throw new NegocioException("La operacion dejaria existencia negativa en " + producto.getNombre());
        }

        MovimientoInventario m = new MovimientoInventario();
        m.setProducto(producto);
        m.setUsuario(usuario);
        m.setTipo(tipo);
        m.setCantidad(cantidad);
        m.setCostoUnitario(costo);
        m.setExistenciaAnterior(anterior);
        m.setExistenciaNueva(nueva);
        m.setOrigenTipo(origenTipo);
        m.setOrigenId(origenId);
        m.setFecha(LocalDateTime.now());
        movimientoRepository.save(m);

        producto.setExistenciaActual(nueva);
    }

    // Bloquea la fila del producto hasta que termine la transaccion
    private Producto bloquearProducto(Integer idProducto) {
        return productoRepository.findByIdParaActualizar(idProducto)
                .orElseThrow(() -> new NoEncontradoException("Producto", idProducto));
    }

    private void validarProducto(Integer idProducto) {
        if (!productoRepository.existsById(idProducto)) {
            throw new NoEncontradoException("Producto", idProducto);
        }
    }
}