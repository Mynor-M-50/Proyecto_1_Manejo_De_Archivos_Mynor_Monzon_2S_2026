package com.minierp.backend.dto.inventario;

import com.minierp.backend.entity.Lote;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// Permite ver las capas de inventario y en que orden las consume PEPS
public record LoteResponse(
        Integer idLote,
        LocalDateTime fechaIngreso,
        Integer cantidadInicial,
        Integer cantidadDisponible,
        BigDecimal costoUnitario
) {
    public static LoteResponse from(Lote l) {
        return new LoteResponse(
                l.getIdLote(),
                l.getFechaIngreso(),
                l.getCantidadInicial(),
                l.getCantidadDisponible(),
                l.getCostoUnitario()
        );
    }
}
