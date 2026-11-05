package com.ejemplo.producto.domain.services;

import com.ejemplo.producto.domain.model.Producto;

import java.util.List;
import java.util.Optional;

/**
 * Contrato del repositorio de productos. Vive en la capa de servicios de dominio:
 * el dominio dice QUÉ necesita y la infraestructura decide CÓMO (JPA, memoria, etc.).
 * Solo conoce el modelo de dominio; no depende de Spring, JPA ni HTTP.
 */
public interface IProductoRepository {

    Optional<Producto> buscarPorId(Long id);

    List<Producto> buscarTodos();

    boolean existePorId(Long id);

    /** Crea o actualiza el producto y lo devuelve con su id asignado. */
    Producto guardar(Producto producto);

    void eliminarPorId(Long id);
}
