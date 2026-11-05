package com.ejemplo.producto.application;

import com.ejemplo.producto.domain.exception.PrecioInvalidoException;
import com.ejemplo.producto.domain.exception.ProductoNoEncontradoException;
import com.ejemplo.producto.domain.model.Categoria;
import com.ejemplo.producto.domain.model.Producto;
import com.ejemplo.producto.domain.services.IProductoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Prueba el servicio de aplicación con el repositorio del dominio simulado (Mockito). */
@ExtendWith(MockitoExtension.class)
class CambiarPrecioProductoTest {

    @Mock
    private IProductoRepository repositorio;

    private CambiarPrecioProducto servicio;

    @BeforeEach
    void setUp() {
        servicio = new CambiarPrecioProducto(repositorio);
    }

    private Producto productoExistente() {
        return Producto.reconstruir(1L, "HOG-0001", "Licuadora", null, new BigDecimal("250000.00"),
                25, Categoria.HOGAR, true, LocalDateTime.now());
    }

    @Test
    void cambiaElPrecioYGuarda() {
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(productoExistente()));
        when(repositorio.guardar(any(Producto.class))).thenAnswer(inv -> inv.getArgument(0));

        Producto resultado = servicio.cambiarPrecio(1L, new BigDecimal("199900"));

        assertThat(resultado.getPrecio()).isEqualByComparingTo("199900.00");
        verify(repositorio).guardar(resultado);
    }

    @Test
    void lanzaErrorSiElProductoNoExiste() {
        when(repositorio.buscarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.cambiarPrecio(99L, BigDecimal.TEN))
                .isInstanceOf(ProductoNoEncontradoException.class);
        verify(repositorio, never()).guardar(any());
    }

    @Test
    void noGuardaSiElPrecioEsInvalido() {
        when(repositorio.buscarPorId(1L)).thenReturn(Optional.of(productoExistente()));

        assertThatThrownBy(() -> servicio.cambiarPrecio(1L, BigDecimal.ZERO))
                .isInstanceOf(PrecioInvalidoException.class);
        verify(repositorio, never()).guardar(any());
    }
}
