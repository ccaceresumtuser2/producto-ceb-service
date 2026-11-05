package com.ejemplo.producto.application;

import com.ejemplo.producto.domain.model.Producto;

/**
 * Contrato del servicio de aplicación para crear un producto.
 * El controlador depende de esta abstracción y no de la clase concreta CrearProducto
 * (principio de inversión de dependencias).
 */
public interface ICrearProducto {

    Producto crear(CrearProductoCommand comando);
}
