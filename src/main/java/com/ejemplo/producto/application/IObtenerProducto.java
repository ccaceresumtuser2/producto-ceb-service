package com.ejemplo.producto.application;

import com.ejemplo.producto.domain.model.Producto;

import java.util.List;

/**
 * Contrato del servicio de aplicación para consultar productos.
 * El controlador depende de esta abstracción y no de la clase concreta ObtenerProducto
 * (principio de inversión de dependencias).
 */
public interface IObtenerProducto {

    Producto obtenerPorId(Long id);

    List<Producto> listar();
}
