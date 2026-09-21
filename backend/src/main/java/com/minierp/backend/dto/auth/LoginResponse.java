package com.minierp.backend.dto.auth;

// El area le sirve a Angular para mostrar solo el menu del rol
public record LoginResponse(
        String token,
        String username,
        String nombreCompleto,
        String area
) {}
