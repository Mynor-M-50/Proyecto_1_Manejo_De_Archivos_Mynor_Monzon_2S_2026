package com.minierp.backend.dto.reporte;

import java.math.BigDecimal;

// Ventas agrupadas por periodo (dia, semana o mes)
public record ResumenPeriodoReporte(
        String periodo,
        Long numeroVentas,
        BigDecimal subtotal,
        BigDecimal iva,
        BigDecimal total
) {}
