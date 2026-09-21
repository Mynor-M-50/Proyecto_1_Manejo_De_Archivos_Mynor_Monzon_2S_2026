package com.minierp.backend.dto.reporte;

// Conteos por producto: mas movimientos o adquiridos con mayor frecuencia
public record ConteoReporte(
        Integer idProducto,
        String codigo,
        String nombre,
        Long cantidad
) {}
