package com.minierp.backend.dto.reporte;

import java.math.BigDecimal;

// Top clientes con mayor monto acumulado de compras
public record ClienteMontoReporte(
        Integer idCliente,
        String nit,
        String nombre,
        Long numeroCompras,
        BigDecimal montoTotal
) {}
