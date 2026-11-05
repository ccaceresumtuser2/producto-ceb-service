package com.ejemplo.producto.application;

import com.ejemplo.producto.domain.model.Producto;
import com.ejemplo.producto.domain.services.IProductoRepository;

/** Servicio de aplicación: crea el producto (aplicando las reglas del dominio) y lo guarda. */
public class CrearProducto implements ICrearProducto {

    private final IProductoRepository repositorio;

    public CrearProducto(IProductoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Producto crear(CrearProductoCommand comando) {
        Producto producto = Producto.crear(comando.codigo(), comando.nombre(), comando.descripcion(),
                comando.precio(), comando.stock(), comando.categoria());
        return repositorio.guardar(producto);
    }
}
