package com.minierp.backend.dto.reporte;

// Productos con menor existencia disponible
public record ExistenciaReporte(
        Integer idProducto,
        String codigo,
        String nombre,
        String categoria,
        Integer existenciaActual,
        Integer stockMinimo
) {}
