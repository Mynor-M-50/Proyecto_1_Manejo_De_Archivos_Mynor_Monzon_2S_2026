package com.minierp.backend.service;

import com.minierp.backend.dto.venta.DetalleVentaRequest;
import com.minierp.backend.dto.venta.VentaRequest;
import com.minierp.backend.dto.venta.VentaResponse;
import com.minierp.backend.entity.Cliente;
import com.minierp.backend.entity.DetalleVenta;
import com.minierp.backend.entity.Producto;
import com.minierp.backend.entity.Usuario;
import com.minierp.backend.entity.Venta;
import com.minierp.backend.exception.NegocioException;
import com.minierp.backend.exception.NoEncontradoException;
import com.minierp.backend.repository.VentaRepository;
import com.minierp.backend.security.UsuarioActual;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Year;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class VentaService {

    private static final String MODULO = "VENTAS";
    private static final BigDecimal TASA_IVA = new BigDecimal("0.12");

    private final VentaRepository ventaRepository;
    private final ClienteService clienteService;
    private final ProductoService productoService;
    private final InventarioService inventarioService;
    private final FacturaPdfService facturaPdfService;
    private final UsuarioActual usuarioActual;
    private final BitacoraService bitacoraService;
    private final EntityManager entityManager;

    public VentaService(VentaRepository ventaRepository,
                        ClienteService clienteService,
                        ProductoService productoService,
                        InventarioService inventarioService,
                        FacturaPdfService facturaPdfService,
                        UsuarioActual usuarioActual,
                        BitacoraService bitacoraService,
                        EntityManager entityManager) {
        this.ventaRepository = ventaRepository;
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.inventarioService = inventarioService;
        this.facturaPdfService = facturaPdfService;
        this.usuarioActual = usuarioActual;
        this.bitacoraService = bitacoraService;
        this.entityManager = entityManager;
    }

    // Todo en una transaccion: si un producto no tiene stock, se deshace la venta completa
    @Transactional
    public VentaResponse registrar(VentaRequest req) {
        Cliente cliente = clienteService.obtener(req.idCliente());
        if (!cliente.getEstado()) {
            throw new NegocioException("El cliente " + cliente.getNombre() + " esta inactivo");
        }
        validarSinDuplicados(req.detalles());
        Usuario usuario = usuarioActual.obtener();

        Venta venta = new Venta();
        venta.setNumeroFactura(siguienteNumeroFactura());
        venta.setCliente(cliente);
        venta.setUsuario(usuario);
        venta.setFecha(LocalDateTime.now());

        // 1. Antes de tocar el inventario, se valida que todas las lineas se puedan vender
        BigDecimal subtotal = BigDecimal.ZERO;
        for (DetalleVentaRequest linea : req.detalles()) {
            Producto producto = productoService.buscar(linea.idProducto());
            if (!producto.getEstado()) {
                throw new NegocioException("El producto " + producto.getNombre() + " no esta a la venta");
            }
            if (producto.getExistenciaActual() < linea.cantidad()) {
                throw new NegocioException("Stock insuficiente para " + producto.getNombre()
                        + ": disponible " + producto.getExistenciaActual() + ", solicitado " + linea.cantidad());
            }

            // El precio sale del producto, no de la peticion
            BigDecimal precio = producto.getPrecioVenta();
            DetalleVenta d = new DetalleVenta();
            d.setProducto(producto);
            d.setCantidad(linea.cantidad());
            d.setPrecioUnitario(precio);
            d.setSubtotal(precio.multiply(BigDecimal.valueOf(linea.cantidad())).setScale(2, RoundingMode.HALF_UP));
            venta.agregarDetalle(d);
            subtotal = subtotal.add(d.getSubtotal());
        }

        BigDecimal iva = subtotal.multiply(TASA_IVA).setScale(2, RoundingMode.HALF_UP);
        venta.setSubtotal(subtotal);
        venta.setIva(iva);
        venta.setTotal(subtotal.add(iva));
        ventaRepository.save(venta);

        // 2. Salida de inventario por PEPS; se guarda el costo real de lo vendido
        for (DetalleVenta d : venta.getDetalles()) {
            BigDecimal costoTotal = inventarioService.registrarSalidaPeps(
                    d.getProducto().getIdProducto(), d.getCantidad(), venta.getIdVenta(), usuario);
            d.setCostoUnitario(costoTotal.divide(BigDecimal.valueOf(d.getCantidad()), 2, RoundingMode.HALF_UP));
        }

        bitacoraService.registrar(MODULO, BitacoraService.CREAR,
                "Venta " + venta.getNumeroFactura() + " a " + cliente.getNombre() + " por Q" + venta.getTotal());
        return VentaResponse.from(venta);
    }

    // Filtro opcional por cliente: permite consultar el historial de un cliente
    @Transactional(readOnly = true)
    public List<VentaResponse> listar(LocalDate desde, LocalDate hasta, Integer idCliente) {
        LocalDateTime inicio = desde != null ? desde.atStartOfDay() : LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime fin = hasta != null ? hasta.plusDays(1).atStartOfDay() : LocalDate.now().plusDays(1).atStartOfDay();

        String jpql = "SELECT v FROM Venta v WHERE v.fecha >= :inicio AND v.fecha < :fin"
                + (idCliente != null ? " AND v.cliente.idCliente = :idCliente" : "")
                + " ORDER BY v.fecha DESC";

        TypedQuery<Venta> query = entityManager.createQuery(jpql, Venta.class)
                .setParameter("inicio", inicio)
                .setParameter("fin", fin);
        if (idCliente != null) {
            query.setParameter("idCliente", idCliente);
        }
        return query.getResultList().stream().map(VentaResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public VentaResponse obtener(Integer id) {
        return VentaResponse.from(buscar(id));
    }

    @Transactional(readOnly = true)
    public byte[] generarFactura(Integer id) {
        return facturaPdfService.generar(buscar(id));
    }

    private Venta buscar(Integer id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new NoEncontradoException("Venta", id));
    }

    private String siguienteNumeroFactura() {
        int siguiente = ventaRepository.findUltimoId() + 1;
        return String.format("FAC-%d%05d", Year.now().getValue(), siguiente);
    }

    private void validarSinDuplicados(List<DetalleVentaRequest> detalles) {
        Set<Integer> vistos = new HashSet<>();
        for (DetalleVentaRequest d : detalles) {
            if (!vistos.add(d.idProducto())) {
                throw new NegocioException("El producto " + d.idProducto() + " aparece repetido en la venta");
            }
        }
    }
}