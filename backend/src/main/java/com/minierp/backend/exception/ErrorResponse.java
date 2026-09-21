package com.minierp.backend.exception;

import java.time.LocalDateTime;
import java.util.List;

// Forma unica de todos los errores que devuelve la API
public record ErrorResponse(
        LocalDateTime fecha,
        int estado,
        String error,
        String mensaje,
        List<String> detalles
) {
    public static ErrorResponse of(int estado, String error, String mensaje) {
        return new ErrorResponse(LocalDateTime.now(), estado, error, mensaje, List.of());
    }

    public static ErrorResponse of(int estado, String error, String mensaje, List<String> detalles) {
        return new ErrorResponse(LocalDateTime.now(), estado, error, mensaje, detalles);
    }
}
