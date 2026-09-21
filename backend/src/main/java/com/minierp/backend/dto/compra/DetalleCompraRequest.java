package com.minierp.backend.dto.compra;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record DetalleCompraRequest(

        @NotNull(message = "El producto es obligatorio")
        Integer idProducto,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a 0")
        Integer cantidad,

        @NotNull(message = "El costo unitario es obligatorio")
        @Positive(message = "El costo unitario debe ser mayor a 0")
        @Digits(integer = 10, fraction = 2, message = "El costo admite maximo 2 decimales")
        BigDecimal costoUnitario
) {}
