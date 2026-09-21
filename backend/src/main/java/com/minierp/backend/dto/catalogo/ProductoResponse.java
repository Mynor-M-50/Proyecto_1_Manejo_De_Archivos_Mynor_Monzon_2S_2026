package com.minierp.backend.dto.catalogo;

import com.minierp.backend.entity.Producto;

import java.math.BigDecimal;

public record ProductoResponse(
        Integer idProducto,
        String codigo,
        String nombre,
        String descripcion,
        String unidadMedida,
        BigDecimal precioVenta,
        Integer stockMinimo,
        Integer existenciaActual,
        Boolean estado,
        Integer idCategoria,
        String categoria,
        boolean stockBajo
) {
    public static ProductoResponse from(Producto p) {
        return new ProductoResponse(
                p.getIdProducto(),
                p.getCodigo(),
                p.getNombre(),
                p.getDescripcion(),
                p.getUnidadMedida(),
                p.getPrecioVenta(),
                p.getStockMinimo(),
                p.getExistenciaActual(),
                p.getEstado(),
                p.getCategoria().getIdCategoria(),
                p.getCategoria().getNombre(),
                p.isStockBajo()
        );
    }
}
