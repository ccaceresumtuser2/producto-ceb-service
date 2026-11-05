package com.ejemplo.producto.application;

import com.ejemplo.producto.domain.exception.ProductoNoEncontradoException;
import com.ejemplo.producto.domain.model.Producto;
import com.ejemplo.producto.domain.services.IProductoRepository;

import java.util.List;

/** Servicio de aplicación: busca el producto o lanza un error si no existe. */
public class ObtenerProducto implements IObtenerProducto {

    private final IProductoRepository repositorio;

    public ObtenerProducto(IProductoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Producto obtenerPorId(Long id) {
        return repositorio.buscarPorId(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
    }

    @Override
    public List<Producto> listar() {
        return repositorio.buscarTodos();
    }
}
