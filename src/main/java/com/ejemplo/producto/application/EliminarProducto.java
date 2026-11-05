package com.ejemplo.producto.application;

import com.ejemplo.producto.domain.exception.ProductoNoEncontradoException;
import com.ejemplo.producto.domain.services.IProductoRepository;

/** Servicio de aplicación: verifica que el producto exista y lo elimina. */
public class EliminarProducto implements IEliminarProducto {

    private final IProductoRepository repositorio;

    public EliminarProducto(IProductoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public void eliminar(Long id) {
        if (!repositorio.existePorId(id)) {
            throw new ProductoNoEncontradoException(id);
        }
        repositorio.eliminarPorId(id);
    }
}
