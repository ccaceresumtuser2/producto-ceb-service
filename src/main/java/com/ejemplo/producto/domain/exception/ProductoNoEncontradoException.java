package com.ejemplo.producto.domain.exception;

/** El producto solicitado no existe. */
public class ProductoNoEncontradoException extends RuntimeException {

    public ProductoNoEncontradoException(Long id) {
        super("No existe un producto con id " + id);
    }
}
