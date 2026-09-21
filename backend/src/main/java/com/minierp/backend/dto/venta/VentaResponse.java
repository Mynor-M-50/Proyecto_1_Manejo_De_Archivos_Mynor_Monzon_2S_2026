package com.minierp.backend.dto.venta;

import com.minierp.backend.entity.DetalleVenta;
import com.minierp.backend.entity.Venta;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VentaResponse(
        Integer idVenta,
        String numeroFactura,
        LocalDateTime fecha,
        BigDecimal subtotal,
        BigDecimal iva,
        BigDecimal total,
        String estado,
        Integer idCliente,
        String cliente,
        String nitCliente,
        String usuario,
        List<Linea> detalles
) {
    public record Linea(
            Integer idProducto,
            String codigo,
            String producto,
            Integer cantidad,
            BigDecimal precioUnitario,
            BigDecimal subtotal
    ) {
        static Linea from(DetalleVenta d) {
            return new Linea(
                    d.getProducto().getIdProducto(),
                    d.getProducto().getCodigo(),
                    d.getProducto().getNombre(),
                    d.getCantidad(),
                    d.getPrecioUnitario(),
                    d.getSubtotal()
            );
        }
    }

    public static VentaResponse from(Venta v) {
        return new VentaResponse(
                v.getIdVenta(),
                v.getNumeroFactura(),
                v.getFecha(),
                v.getSubtotal(),
                v.getIva(),
                v.getTotal(),
                v.getEstado(),
                v.getCliente().getIdCliente(),
                v.getCliente().getNombre(),
                v.getCliente().getNit(),
                v.getUsuario().getNombreCompleto(),
                v.getDetalles().stream().map(Linea::from).toList()
        );
    }
}
