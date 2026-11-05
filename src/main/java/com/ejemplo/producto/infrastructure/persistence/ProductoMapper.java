package com.ejemplo.producto.infrastructure.persistence;

import com.ejemplo.producto.domain.model.Producto;
import org.springframework.stereotype.Component;

/** Convierte entre el modelo del dominio (Producto) y la entidad JPA (ProductoEntity). */
@Component
public class ProductoMapper {

    public ProductoEntity aEntidad(Producto p) {
        return new ProductoEntity(p.getId(), p.getCodigo(), p.getNombre(), p.getDescripcion(),
                p.getPrecio(), p.getStock(), p.getCategoria(), p.isActivo(), p.getFechaCreacion());
    }

    public Producto aDominio(ProductoEntity e) {
        return Producto.reconstruir(e.getId(), e.getCodigo(), e.getNombre(), e.getDescripcion(),
                e.getPrecio(), e.getStock(), e.getCategoria(), e.getActivo(), e.getFechaCreacion());
    }
}
