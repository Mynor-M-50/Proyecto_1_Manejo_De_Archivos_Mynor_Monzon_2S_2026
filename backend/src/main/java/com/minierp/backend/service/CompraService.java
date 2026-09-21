package com.minierp.backend.service;

import com.minierp.backend.dto.compra.CompraRequest;
import com.minierp.backend.dto.compra.CompraResponse;
import com.minierp.backend.dto.compra.DetalleCompraRequest;
import com.minierp.backend.entity.Compra;
import com.minierp.backend.entity.DetalleCompra;
import com.minierp.backend.entity.Producto;
import com.minierp.backend.entity.Proveedor;
import com.minierp.backend.entity.Usuario;
import com.minierp.backend.exception.NegocioException;
import com.minierp.backend.exception.NoEncontradoException;
import com.minierp.backend.repository.CompraRepository;
import com.minierp.backend.security.UsuarioActual;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CompraService {

    private static final String MODULO = "COMPRAS";

    private final CompraRepository compraRepository;
    private final ProveedorService proveedorService;
    private final ProductoService productoService;
    private final InventarioService inventarioService;
    private final UsuarioActual usuarioActual;
    private final BitacoraService bitacoraService;

    public CompraService(CompraRepository compraRepository,
                         ProveedorService proveedorService,
                         ProductoService productoService,
                         InventarioService inventarioService,
                         UsuarioActual usuarioActual,
                         BitacoraService bitacoraService) {
        this.compraRepository = compraRepository;
        this.proveedorService = proveedorService;
        this.productoService = productoService;
        this.inventarioService = inventarioService;
        this.usuarioActual = usuarioActual;
        this.bitacoraService = bitacoraService;
    }

    // Todo en una transaccion: si falla cualquier linea, no queda ni la compra ni los lotes
    @Transactional
    public CompraResponse registrar(CompraRequest req) {
        Proveedor proveedor = proveedorService.obtener(req.idProveedor());
        if (!proveedor.getEstado()) {
            throw new NegocioException("El proveedor " + proveedor.getNombre() + " esta inactivo");
        }
        validarSinDuplicados(req.detalles());
        Usuario usuario = usuarioActual.obtener();

        Compra compra = new Compra();
        compra.setNumeroDoc(req.numeroDoc().trim().toUpperCase());
        compra.setProveedor(proveedor);
        compra.setUsuario(usuario);
        compra.setFecha(LocalDateTime.now());

        BigDecimal total = BigDecimal.ZERO;
        for (DetalleCompraRequest linea : req.detalles()) {
            Producto producto = productoService.buscar(linea.idProducto());
            if (!producto.getEstado()) {
                throw new NegocioException("El producto " + producto.getNombre() + " esta inactivo");
            }
            DetalleCompra d = new DetalleCompra();
            d.setProducto(producto);
            d.setCantidad(linea.cantidad());
            d.setCostoUnitario(linea.costoUnitario());
            d.setSubtotal(linea.costoUnitario()
                    .multiply(BigDecimal.valueOf(linea.cantidad()))
                    .setScale(2, RoundingMode.HALF_UP));
            compra.agregarDetalle(d);
            total = total.add(d.getSubtotal());
        }
        compra.setTotal(total);

        // El cascade guarda tambien los detalles
        compraRepository.save(compra);

        // Cada detalle genera su lote y su movimiento de entrada
        for (DetalleCompra d : compra.getDetalles()) {
            inventarioService.registrarEntrada(d.getProducto().getIdProducto(), d.getCantidad(),
                    d.getCostoUnitario(), d, compra.getIdCompra(), usuario);
        }

        bitacoraService.registrar(MODULO, BitacoraService.CREAR,
                "Compra " + compra.getNumeroDoc() + " a " + proveedor.getNombre() + " por Q" + total);
        return CompraResponse.from(compra);
    }

    @Transactional(readOnly = true)
    public List<CompraResponse> listar(LocalDate desde, LocalDate hasta) {
        LocalDateTime inicio = desde != null ? desde.atStartOfDay() : LocalDate.of(2000, 1, 1).atStartOfDay();
        LocalDateTime fin = hasta != null ? hasta.plusDays(1).atStartOfDay() : LocalDate.now().plusDays(1).atStartOfDay();
        return compraRepository.findByFechaBetweenOrderByFechaDesc(inicio, fin)
                .stream().map(CompraResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public CompraResponse obtener(Integer id) {
        return compraRepository.findById(id)
                .map(CompraResponse::from)
                .orElseThrow(() -> new NoEncontradoException("Compra", id));
    }

    private void validarSinDuplicados(List<DetalleCompraRequest> detalles) {
        Set<Integer> vistos = new HashSet<>();
        for (DetalleCompraRequest d : detalles) {
            if (!vistos.add(d.idProducto())) {
                throw new NegocioException("El producto " + d.idProducto() + " aparece repetido en la compra");
            }
        }
    }
}