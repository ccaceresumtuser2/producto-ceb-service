package com.ejemplo.producto.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

/** Nuevo precio recibido. Que sea mayor que cero lo valida el dominio. */
@Schema(description = "Nuevo precio del producto")
public record CambiarPrecioRequest(
        @Schema(description = "Mayor que 0, máximo 2 decimales", example = "159900.00", minimum = "0.01")
        @NotNull
        BigDecimal precio
) {
}
