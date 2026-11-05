package com.ejemplo.producto.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/** Operaciones de base de datos generadas por Spring Data JPA. */
public interface ProductoJpaRepository extends JpaRepository<ProductoEntity, Long> {
}
