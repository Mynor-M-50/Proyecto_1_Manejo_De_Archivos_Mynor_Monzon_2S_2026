package com.minierp.backend.dto.reporte;

import java.math.BigDecimal;

// Top productos mas vendidos (por cantidad) y con mas ingresos (por monto)
public record ProductoVendidoReporte(
        Integer idProducto,
        String codigo,
        String nombre,
        Long cantidadVendida,
        BigDecimal ingresos
) {}
