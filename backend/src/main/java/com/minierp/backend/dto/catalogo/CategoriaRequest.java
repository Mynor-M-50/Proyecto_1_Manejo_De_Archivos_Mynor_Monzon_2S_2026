package com.minierp.backend.dto.catalogo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no puede exceder 80 caracteres")
        String nombre,

        @Size(max = 200, message = "La descripcion no puede exceder 200 caracteres")
        String descripcion
) {}
