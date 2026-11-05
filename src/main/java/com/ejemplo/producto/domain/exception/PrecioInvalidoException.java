package com.ejemplo.producto.domain.exception;

/** El precio no cumple las reglas de negocio. */
public class PrecioInvalidoException extends RuntimeException {

    public PrecioInvalidoException(String mensaje) {
        super(mensaje);
    }
}
