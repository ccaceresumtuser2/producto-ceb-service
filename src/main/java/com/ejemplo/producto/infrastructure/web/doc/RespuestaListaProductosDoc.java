package com.ejemplo.producto.infrastructure.web.doc;

import com.ejemplo.producto.infrastructure.web.dto.ProductoResponse;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Solo para Swagger: representa RespuestaApi&lt;List&lt;ProductoResponse&gt;&gt;.
 */
@Schema(name = "RespuestaListaProductos", description = "RespuestaApi con una lista de productos en data")
public record RespuestaListaProductosDoc(
        @Schema(example = "Se encontraron 1 productos") String message,
        @Schema(example = "2026-11-04T16:40:00.123") LocalDateTime timestamp,
        List<ProductoResponse> data,
        @Schema(example = "200") int status
) {
}
