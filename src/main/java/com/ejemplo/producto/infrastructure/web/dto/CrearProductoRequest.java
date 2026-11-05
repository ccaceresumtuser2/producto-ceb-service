package com.ejemplo.producto.infrastructure.web.dto;

import com.ejemplo.producto.domain.model.Categoria;
import com.ejemplo.producto.application.CrearProductoCommand;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/** Datos recibidos al crear un producto. Las reglas del precio las valida el dominio. */
@Schema(description = "Datos para crear un producto")
public record CrearProductoRequest(
        @Schema(description = "3 letras mayúsculas, guion y 4 dígitos. Debe ser único",
                example = "TEC-0010", pattern = "^[A-Z]{3}-[0-9]{4}$")
        @NotBlank
        @Pattern(regexp = "^[A-Z]{3}-[0-9]{4}$", message = "debe tener el formato AAA-0000")
        String codigo,

        @Schema(description = "Nombre del producto", example = "Teclado mecánico Redragon", maxLength = 100)
        @NotBlank
        @Size(max = 100)
        String nombre,

        @Schema(description = "Opcional", example = "Teclado RGB switches rojos, español", maxLength = 255)
        @Size(max = 255)
        String descripcion,

        @Schema(description = "Mayor que 0, máximo 2 decimales", example = "189900.00", minimum = "0.01")
        @NotNull
        BigDecimal precio,

        @Schema(description = "Unidades disponibles (0 o más)", example = "15", minimum = "0")
        @NotNull
        @PositiveOrZero
        Integer stock,

        @Schema(description = "Categoría del producto", example = "TECNOLOGIA")
        @NotNull
        Categoria categoria
) {

    /** Traduce el formato de la API al comando que entiende el núcleo. */
    public CrearProductoCommand aComando() {
        return new CrearProductoCommand(codigo, nombre, descripcion, precio, stock, categoria);
    }
}
