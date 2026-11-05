package com.ejemplo.producto.infrastructure.web;

import com.ejemplo.producto.infrastructure.web.dto.RespuestaApi;
import com.ejemplo.producto.domain.exception.PrecioInvalidoException;
import com.ejemplo.producto.domain.exception.ProductoNoEncontradoException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Convierte las excepciones en respuestas HTTP con el formato estándar { message, timestamp, data, status }.
 * En los errores, data es null, salvo en los de validación, donde trae el detalle por campo.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // --- Errores del dominio ---

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<RespuestaApi<Void>> productoNoEncontrado(ProductoNoEncontradoException ex) {
        return RespuestaApi.error(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler(PrecioInvalidoException.class)
    public ResponseEntity<RespuestaApi<Void>> precioInvalido(PrecioInvalidoException ex) {
        return RespuestaApi.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaApi<Void>> datosInvalidos(IllegalArgumentException ex) {
        return RespuestaApi.error(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    // --- Errores de la petición HTTP ---

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaApi<Map<String, String>>> validacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new LinkedHashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(e -> errores.putIfAbsent(e.getField(), e.getDefaultMessage()));
        return RespuestaApi.de(HttpStatus.BAD_REQUEST, errores, "Datos inválidos");
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaApi<Void>> cuerpoInvalido(HttpMessageNotReadableException ex) {
        return RespuestaApi.error(HttpStatus.BAD_REQUEST,
                "El cuerpo de la petición no es un JSON válido o tiene un valor no permitido (revise categoria y tipos)");
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<RespuestaApi<Void>> tipoInvalido(MethodArgumentTypeMismatchException ex) {
        return RespuestaApi.error(HttpStatus.BAD_REQUEST,
                "El parámetro '" + ex.getName() + "' tiene un valor inválido: " + ex.getValue());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<RespuestaApi<Void>> metodoNoPermitido(HttpRequestMethodNotSupportedException ex) {
        return RespuestaApi.error(HttpStatus.METHOD_NOT_ALLOWED,
                "Método " + ex.getMethod() + " no permitido en esta ruta");
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RespuestaApi<Void>> rutaNoEncontrada(NoResourceFoundException ex) {
        return RespuestaApi.error(HttpStatus.NOT_FOUND, "La ruta /" + ex.getResourcePath() + " no existe");
    }

    // --- Errores de persistencia ---

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<RespuestaApi<Void>> conflicto(DataIntegrityViolationException ex) {
        return RespuestaApi.error(HttpStatus.CONFLICT,
                "Ya existe un producto con ese código o los datos violan una restricción");
    }

    // --- Cualquier otro error ---

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaApi<Void>> errorInesperado(Exception ex) {
        log.error("Error no controlado", ex);
        return RespuestaApi.error(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno del servidor");
    }
}
