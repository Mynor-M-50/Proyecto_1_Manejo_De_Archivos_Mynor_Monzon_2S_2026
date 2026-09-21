package com.minierp.backend.dto.catalogo;

import com.minierp.backend.entity.Usuario;

import java.time.LocalDateTime;

// Nunca incluye el hash de la contrasena
public record UsuarioResponse(
        Integer idUsuario,
        String nombreCompleto,
        String username,
        Boolean estado,
        LocalDateTime fechaCreacion,
        Integer idArea,
        String area
) {
    public static UsuarioResponse from(Usuario u) {
        return new UsuarioResponse(
                u.getIdUsuario(),
                u.getNombreCompleto(),
                u.getUsername(),
                u.getEstado(),
                u.getFechaCreacion(),
                u.getArea().getIdArea(),
                u.getArea().getNombre()
        );
    }
}
