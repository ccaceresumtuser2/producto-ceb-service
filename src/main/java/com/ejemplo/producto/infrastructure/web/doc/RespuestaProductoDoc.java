package com.ejemplo.producto.infrastructure.web.doc;

import com.ejemplo.producto.infrastructure.web.dto.ProductoResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

/**
 * Solo para Swagger: representa RespuestaApi&lt;ProductoResponse&gt;.
 * Las anotaciones de Java no aceptan tipos genéricos, por eso se declara aparte.
 */
@Schema(name = "RespuestaProducto", description = "RespuestaApi con un producto en data")
public record RespuestaProductoDoc(
        @Schema(example = "Producto encontrado") String message,
        @Schema(example = "2026-11-04T16:40:00.123") LocalDateTime timestamp,
        ProductoResponse data,
        @Schema(example = "200") int status
) {
}
