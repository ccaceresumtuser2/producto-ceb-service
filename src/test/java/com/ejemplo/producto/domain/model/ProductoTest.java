package com.ejemplo.producto.domain.model;

import com.ejemplo.producto.domain.exception.PrecioInvalidoException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ProductoTest {

    private Producto productoValido() {
        return Producto.crear("TEC-0001", "Teclado", "Teclado mecánico",
                new BigDecimal("120000"), 10, Categoria.TECNOLOGIA);
    }

    @Test
    void crearProductoNuevoQuedaActivoSinIdYConFecha() {
        Producto producto = productoValido();

        assertThat(producto.getId()).isNull();
        assertThat(producto.isActivo()).isTrue();
        assertThat(producto.getFechaCreacion()).isNotNull();
        assertThat(producto.getPrecio()).isEqualByComparingTo("120000.00");
        assertThat(producto.getPrecio().scale()).isEqualTo(2);
    }

    @Test
    void rechazaPrecioCeroONegativo() {
        assertThatThrownBy(() -> Producto.crear("TEC-0001", "Teclado", null,
                BigDecimal.ZERO, 1, Categoria.TECNOLOGIA))
                .isInstanceOf(PrecioInvalidoException.class);

        assertThatThrownBy(() -> Producto.crear("TEC-0001", "Teclado", null,
                new BigDecimal("-5"), 1, Categoria.TECNOLOGIA))
                .isInstanceOf(PrecioInvalidoException.class);
    }

    @Test
    void rechazaPrecioConMasDeDosDecimales() {
        assertThatThrownBy(() -> Producto.crear("TEC-0001", "Teclado", null,
                new BigDecimal("10.999"), 1, Categoria.TECNOLOGIA))
                .isInstanceOf(PrecioInvalidoException.class);
    }

    @Test
    void cambiarPrecioActualizaElValor() {
        Producto producto = productoValido();

        producto.cambiarPrecio(new BigDecimal("99999.90"));

        assertThat(producto.getPrecio()).isEqualByComparingTo("99999.90");
    }

    @Test
    void cambiarPrecioInvalidoNoModificaElPrecio() {
        Producto producto = productoValido();

        assertThatThrownBy(() -> producto.cambiarPrecio(new BigDecimal("-1")))
                .isInstanceOf(PrecioInvalidoException.class);
        assertThat(producto.getPrecio()).isEqualByComparingTo("120000.00");
    }

    @Test
    void rechazaCodigoConFormatoIncorrecto() {
        assertThatThrownBy(() -> Producto.crear("tec-1", "Teclado", null,
                BigDecimal.TEN, 1, Categoria.TECNOLOGIA))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rechazaNombreVacioYStockNegativo() {
        assertThatThrownBy(() -> Producto.crear("TEC-0001", "  ", null,
                BigDecimal.TEN, 1, Categoria.TECNOLOGIA))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> Producto.crear("TEC-0001", "Teclado", null,
                BigDecimal.TEN, -1, Categoria.TECNOLOGIA))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
