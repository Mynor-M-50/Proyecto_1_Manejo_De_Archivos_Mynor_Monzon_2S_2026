package com.minierp.backend.dto.catalogo;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

// No incluye existencia: el stock solo cambia por compras y ventas,
// nunca se edita a mano, para que siempre tenga un movimiento que lo respalde.
public record ProductoRequest(

        @NotBlank(message = "El codigo es obligatorio")
        @Size(max = 30, message = "El codigo no puede exceder 30 caracteres")
        String codigo,

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 120, message = "El nombre no puede exceder 120 caracteres")
        String nombre,

        @Size(max = 250, message = "La descripcion no puede exceder 250 caracteres")
        String descripcion,

        @Size(max = 20, message = "La unidad de medida no puede exceder 20 caracteres")
        String unidadMedida,

        @NotNull(message = "El precio de venta es obligatorio")
        @Positive(message = "El precio de venta debe ser mayor a 0")
        @Digits(integer = 10, fraction = 2, message = "El precio admite maximo 2 decimales")
        BigDecimal precioVenta,

        @NotNull(message = "El stock minimo es obligatorio")
        @PositiveOrZero(message = "El stock minimo no puede ser negativo")
        Integer stockMinimo,

        @NotNull(message = "La categoria es obligatoria")
        Integer idCategoria
) {}
