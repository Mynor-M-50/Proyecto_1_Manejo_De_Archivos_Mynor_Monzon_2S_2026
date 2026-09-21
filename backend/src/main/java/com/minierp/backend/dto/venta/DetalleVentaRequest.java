package com.minierp.backend.dto.venta;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

// No recibe precio: se toma del producto en el servidor,
// asi el cliente no puede alterar el precio desde el navegador.
public record DetalleVentaRequest(

        @NotNull(message = "El producto es obligatorio")
        Integer idProducto,

        @NotNull(message = "La cantidad es obligatoria")
        @Positive(message = "La cantidad debe ser mayor a 0")
        Integer cantidad
) {}
