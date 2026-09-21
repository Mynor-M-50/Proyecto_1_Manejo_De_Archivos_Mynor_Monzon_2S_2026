package com.minierp.backend.dto.compra;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CompraRequest(

        @NotNull(message = "El proveedor es obligatorio")
        Integer idProveedor,

        @NotBlank(message = "El numero de documento es obligatorio")
        @Size(max = 30, message = "El numero de documento no puede exceder 30 caracteres")
        String numeroDoc,

        // @Valid hace que tambien se validen los campos de cada detalle
        @NotEmpty(message = "La compra debe tener al menos un producto")
        List<@Valid DetalleCompraRequest> detalles
) {}
