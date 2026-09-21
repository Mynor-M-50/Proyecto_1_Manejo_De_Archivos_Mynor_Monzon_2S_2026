package com.minierp.backend.dto.inventario;

import com.minierp.backend.entity.MovimientoInventario;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record MovimientoResponse(
        Integer idMovimiento,
        LocalDateTime fecha,
        String tipo,
        Integer cantidad,
        BigDecimal costoUnitario,
        Integer existenciaAnterior,
        Integer existenciaNueva,
        String origenTipo,
        Integer origenId,
        String usuario
) {
    public static MovimientoResponse from(MovimientoInventario m) {
        return new MovimientoResponse(
                m.getIdMovimiento(),
                m.getFecha(),
                m.getTipo(),
                m.getCantidad(),
                m.getCostoUnitario(),
                m.getExistenciaAnterior(),
                m.getExistenciaNueva(),
                m.getOrigenTipo(),
                m.getOrigenId(),
                m.getUsuario().getNombreCompleto()
        );
    }
}
