package com.ejemplo.producto.infrastructure.web.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;

/**
 * Formato único de todas las respuestas de la API, tanto de éxito como de error.
 *
 * <pre>
 * {
 *   "message": "Producto encontrado",
 *   "timestamp": "2026-11-04T16:45:12.123",
 *   "data": { ... },
 *   "status": 200
 * }
 * </pre>
 *
 * @param <T> tipo del contenido de {@code data}
 */
@Schema(description = "Respuesta estándar de la API")
public record RespuestaApi<T>(
        @Schema(description = "Mensaje para el cliente", example = "Operación realizada correctamente")
        String message,

        @Schema(description = "Fecha y hora en que se generó la respuesta", example = "2026-11-04T16:45:12.123")
        LocalDateTime timestamp,

        @Schema(description = "Contenido de la respuesta. Es null cuando no hay nada que devolver")
        T data,

        @Schema(description = "Código HTTP de la respuesta", example = "200")
        int status
) {

    /** Crea el cuerpo de la respuesta con la fecha y hora actuales. */
    public static <T> RespuestaApi<T> nueva(HttpStatus estado, T data, String mensaje) {
        return new RespuestaApi<>(mensaje, LocalDateTime.now(), data, estado.value());
    }

    public static <T> ResponseEntity<RespuestaApi<T>> de(HttpStatus estado, T data, String mensaje) {
        return ResponseEntity.status(estado).body(nueva(estado, data, mensaje));
    }

    public static <T> ResponseEntity<RespuestaApi<T>> ok(T data, String mensaje) {
        return de(HttpStatus.OK, data, mensaje);
    }

    public static <T> ResponseEntity<RespuestaApi<T>> error(HttpStatus estado, String mensaje) {
        return de(estado, null, mensaje);
    }
}
