package com.minierp.backend.dto.compra;

import com.minierp.backend.entity.Compra;
import com.minierp.backend.entity.DetalleCompra;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CompraResponse(
        Integer idCompra,
        String numeroDoc,
        LocalDateTime fecha,
        BigDecimal total,
        String estado,
        Integer idProveedor,
        String proveedor,
        String usuario,
        List<Linea> detalles
) {
    public record Linea(
            Integer idProducto,
            String codigo,
            String producto,
            Integer cantidad,
            BigDecimal costoUnitario,
            BigDecimal subtotal
    ) {
        static Linea from(DetalleCompra d) {
            return new Linea(
                    d.getProducto().getIdProducto(),
                    d.getProducto().getCodigo(),
                    d.getProducto().getNombre(),
                    d.getCantidad(),
                    d.getCostoUnitario(),
                    d.getSubtotal()
            );
        }
    }

    public static CompraResponse from(Compra c) {
        return new CompraResponse(
                c.getIdCompra(),
                c.getNumeroDoc(),
                c.getFecha(),
                c.getTotal(),
                c.getEstado(),
                c.getProveedor().getIdProveedor(),
                c.getProveedor().getNombre(),
                c.getUsuario().getNombreCompleto(),
                c.getDetalles().stream().map(Linea::from).toList()
        );
    }
}
