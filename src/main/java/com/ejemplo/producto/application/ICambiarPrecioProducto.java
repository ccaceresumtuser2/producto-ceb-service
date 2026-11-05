package com.ejemplo.producto.application;

import com.ejemplo.producto.domain.model.Producto;

import java.math.BigDecimal;

/**
 * Contrato del servicio de aplicación para cambiar el precio de un producto.
 * El controlador depende de esta abstracción y no de la clase concreta CambiarPrecioProducto
 * (principio de inversión de dependencias).
 */
public interface ICambiarPrecioProducto {

    Producto cambiarPrecio(Long id, BigDecimal nuevoPrecio);
}
