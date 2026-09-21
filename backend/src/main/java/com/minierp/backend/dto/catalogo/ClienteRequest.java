package com.minierp.backend.dto.catalogo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ClienteRequest(

        @NotBlank(message = "El NIT es obligatorio")
        @Size(max = 20, message = "El NIT no puede exceder 20 caracteres")
        String nit,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede exceder 120 caracteres")
        String nombre,

        @Size(max = 20, message = "El telefono no puede exceder 20 caracteres")
        String telefono,

        @Email(message = "El email no es valido")
        @Size(max = 120, message = "El email no puede exceder 120 caracteres")
        String email,

        @Size(max = 200, message = "La direccion no puede exceder 200 caracteres")
        String direccion
) {}
