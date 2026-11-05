package com.ejemplo.producto.infrastructure.persistence;

import com.ejemplo.producto.domain.model.Categoria;
import com.ejemplo.producto.domain.model.Producto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Prueba la implementación del repositorio contra H2 en memoria (modo PostgreSQL).
 * Flyway crea la tabla con la misma migración V1 que se usa en producción.
 * Cada prueba corre en una transacción que se revierte al final, así que no deja datos.
 */
@DataJpaTest
// NONE: usa la H2 configurada en src/test/resources/application.yml (con Flyway),
// en vez de una base embebida que Spring crearía por su cuenta.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import({ProductoRepositoryJpa.class, ProductoMapper.class})
class ProductoRepositoryJpaTest {

    @Autowired
    private ProductoRepositoryJpa repositorio;

    private Producto nuevo(String codigo) {
        return Producto.crear(codigo, "Producto de prueba", null,
                new BigDecimal("1234.56"), 3, Categoria.OTROS);
    }

    @Test
    void guardarAsignaIdYSePuedeBuscar() {
        Producto guardado = repositorio.guardar(nuevo("ZZZ-9991"));

        assertThat(guardado.getId()).isNotNull();
        assertThat(repositorio.buscarPorId(guardado.getId()))
                .hasValueSatisfying(p -> {
                    assertThat(p.getCodigo()).isEqualTo("ZZZ-9991");
                    assertThat(p.getPrecio()).isEqualByComparingTo("1234.56");
                    assertThat(p.getCategoria()).isEqualTo(Categoria.OTROS);
                });
    }

    @Test
    void guardarProductoExistenteActualizaElPrecio() {
        Producto guardado = repositorio.guardar(nuevo("ZZZ-9992"));

        guardado.cambiarPrecio(new BigDecimal("999.99"));
        repositorio.guardar(guardado);

        assertThat(repositorio.buscarPorId(guardado.getId()))
                .hasValueSatisfying(p -> assertThat(p.getPrecio()).isEqualByComparingTo("999.99"));
    }

    @Test
    void eliminarQuitaElProducto() {
        Producto guardado = repositorio.guardar(nuevo("ZZZ-9993"));

        repositorio.eliminarPorId(guardado.getId());

        assertThat(repositorio.existePorId(guardado.getId())).isFalse();
    }

    @Test
    void codigoDuplicadoViolaElIndiceUnico() {
        repositorio.guardar(nuevo("ZZZ-9994"));

        assertThatThrownBy(() -> repositorio.guardar(nuevo("ZZZ-9994")))
                .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void buscarTodosIncluyeLosGuardados() {
        Producto guardado = repositorio.guardar(nuevo("ZZZ-9995"));

        assertThat(repositorio.buscarTodos())
                .extracting(Producto::getId)
                .contains(guardado.getId());
    }
}
