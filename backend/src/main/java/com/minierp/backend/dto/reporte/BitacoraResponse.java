package com.minierp.backend.dto.reporte;

import com.minierp.backend.entity.Bitacora;

import java.time.LocalDateTime;

// Registro del reporte de logs
public record BitacoraResponse(
        Integer idLog,
        LocalDateTime fechaHora,
        String modulo,
        String accion,
        String descripcion,
        String usuario
) {
    public static BitacoraResponse from(Bitacora b) {
        return new BitacoraResponse(
                b.getIdLog(),
                b.getFechaHora(),
                b.getModulo(),
                b.getAccion(),
                b.getDescripcion(),
                b.getUsuario().getUsername()
        );
    }
}
