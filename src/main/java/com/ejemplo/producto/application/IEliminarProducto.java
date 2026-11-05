package com.ejemplo.producto.application;

/**
 * Contrato del servicio de aplicación para eliminar un producto.
 * El controlador depende de esta abstracción y no de la clase concreta EliminarProducto
 * (principio de inversión de dependencias).
 */
public interface IEliminarProducto {

    void eliminar(Long id);
}
