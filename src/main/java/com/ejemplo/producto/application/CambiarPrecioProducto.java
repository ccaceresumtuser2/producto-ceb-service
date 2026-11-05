package com.ejemplo.producto.application;

import com.ejemplo.producto.domain.exception.ProductoNoEncontradoException;
import com.ejemplo.producto.domain.model.Producto;
import com.ejemplo.producto.domain.services.IProductoRepository;

import java.math.BigDecimal;

/** Servicio de aplicación: busca el producto, cambia el precio y lo guarda. */
public class CambiarPrecioProducto implements ICambiarPrecioProducto {

    private final IProductoRepository repositorio;

    public CambiarPrecioProducto(IProductoRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    public Producto cambiarPrecio(Long id, BigDecimal nuevoPrecio) {
        Producto producto = repositorio.buscarPorId(id)
                .orElseThrow(() -> new ProductoNoEncontradoException(id));
        producto.cambiarPrecio(nuevoPrecio);
        return repositorio.guardar(producto);
    }
}
