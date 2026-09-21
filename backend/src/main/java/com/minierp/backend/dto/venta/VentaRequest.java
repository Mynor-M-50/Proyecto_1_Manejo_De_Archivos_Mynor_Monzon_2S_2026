package com.minierp.backend.dto.venta;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VentaRequest(

        @NotNull(message = "El cliente es obligatorio")
        Integer idCliente,

        @NotEmpty(message = "La venta debe tener al menos un producto")
        List<@Valid DetalleVentaRequest> detalles
) {}
