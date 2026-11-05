package com.ejemplo.producto.infrastructure.persistence;

import com.ejemplo.producto.domain.model.Producto;
import com.ejemplo.producto.domain.services.IProductoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Implementa el contrato del dominio (IProductoRepository) con Spring Data JPA.
 * La dependencia apunta hacia adentro: la infraestructura conoce al dominio, nunca al revés.
 */
@Repository
public class ProductoRepositoryJpa implements IProductoRepository {

    private final ProductoJpaRepository jpaRepository;
    private final ProductoMapper mapper;

    public ProductoRepositoryJpa(ProductoJpaRepository jpaRepository, ProductoMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<Producto> buscarPorId(Long id) {
        return jpaRepository.findById(id).map(mapper::aDominio);
    }

    @Override
    public List<Producto> buscarTodos() {
        return jpaRepository.findAll().stream().map(mapper::aDominio).toList();
    }

    @Override
    public boolean existePorId(Long id) {
        return jpaRepository.existsById(id);
    }

    @Override
    public Producto guardar(Producto producto) {
        ProductoEntity guardada = jpaRepository.saveAndFlush(mapper.aEntidad(producto));
        return mapper.aDominio(guardada);
    }

    @Override
    public void eliminarPorId(Long id) {
        jpaRepository.deleteById(id);
    }
}
