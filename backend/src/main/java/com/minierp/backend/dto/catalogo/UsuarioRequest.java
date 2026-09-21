package com.minierp.backend.dto.catalogo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

// La contrasena llega en texto plano solo aqui; el servicio la guarda como hash BCrypt
public record UsuarioRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede exceder 120 caracteres")
        String nombreCompleto,

        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 4, max = 40, message = "El usuario debe tener entre 4 y 40 caracteres")
        String username,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 6, max = 60, message = "La contrasena debe tener entre 6 y 60 caracteres")
        String password,

        @NotNull(message = "El area es obligatoria")
        Integer idArea
) {}
