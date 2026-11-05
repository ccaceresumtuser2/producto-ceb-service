package com.ejemplo.producto.infrastructure.config;

import com.ejemplo.producto.application.CambiarPrecioProducto;
import com.ejemplo.producto.application.CrearProducto;
import com.ejemplo.producto.application.EliminarProducto;
import com.ejemplo.producto.application.ICambiarPrecioProducto;
import com.ejemplo.producto.application.ICrearProducto;
import com.ejemplo.producto.application.IEliminarProducto;
import com.ejemplo.producto.application.IObtenerProducto;
import com.ejemplo.producto.application.ObtenerProducto;
import com.ejemplo.producto.domain.services.IProductoRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Registra los servicios de aplicación como beans de Spring.
 * Así las capas internas (domain y application) no necesitan anotaciones de Spring.
 * Spring inyecta aquí la implementación de IProductoRepository (ProductoRepositoryJpa).
 */
@Configuration
public class BeanConfig {

    @Bean
    public ICrearProducto crearProducto(IProductoRepository repositorio) {
        return new CrearProducto(repositorio);
    }

    @Bean
    public IObtenerProducto obtenerProducto(IProductoRepository repositorio) {
        return new ObtenerProducto(repositorio);
    }

    @Bean
    public ICambiarPrecioProducto cambiarPrecioProducto(IProductoRepository repositorio) {
        return new CambiarPrecioProducto(repositorio);
    }

    @Bean
    public IEliminarProducto eliminarProducto(IProductoRepository repositorio) {
        return new EliminarProducto(repositorio);
    }
}
