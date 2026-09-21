package com.minierp.backend.dto.reporte;

import java.math.BigDecimal;

// Top proveedores con mayor monto acumulado de compras
public record ProveedorMontoReporte(
        Integer idProveedor,
        String nit,
        String nombre,
        Long numeroCompras,
        BigDecimal montoTotal
) {}
