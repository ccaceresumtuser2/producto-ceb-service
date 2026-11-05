package com.ejemplo.producto.domain.model;

import com.ejemplo.producto.domain.exception.PrecioInvalidoException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Producto del dominio. Contiene los datos y las reglas de negocio.
 * No depende de Spring, JPA ni HTTP.
 */
public class Producto {

    private static final Pattern PATRON_CODIGO = Pattern.compile("^[A-Z]{3}-[0-9]{4}$");
    private static final BigDecimal PRECIO_MAXIMO = new BigDecimal("9999999999.99");

    private final Long id;
    private final String codigo;
    private final String nombre;
    private final String descripcion;
    private BigDecimal precio;
    private final int stock;
    private final Categoria categoria;
    private final boolean activo;
    private final LocalDateTime fechaCreacion;

    private Producto(Long id, String codigo, String nombre, String descripcion, BigDecimal precio,
                     int stock, Categoria categoria, boolean activo, LocalDateTime fechaCreacion) {
        this.id = id;
        this.codigo = validarCodigo(codigo);
        this.nombre = validarNombre(nombre);
        this.descripcion = validarDescripcion(descripcion);
        this.precio = validarPrecio(precio);
        this.stock = validarStock(stock);
        this.categoria = Objects.requireNonNull(categoria, "La categoría es obligatoria");
        this.activo = activo;
        this.fechaCreacion = Objects.requireNonNull(fechaCreacion, "La fecha de creación es obligatoria");
    }

    /** Crea un producto nuevo: sin id, activo y con la fecha actual. */
    public static Producto crear(String codigo, String nombre, String descripcion,
                                 BigDecimal precio, int stock, Categoria categoria) {
        return new Producto(null, codigo, nombre, descripcion, precio, stock, categoria,
                true, LocalDateTime.now());
    }

    /** Reconstruye un producto que ya existe (por ejemplo, leído de la base de datos). */
    public static Producto reconstruir(Long id, String codigo, String nombre, String descripcion,
                                       BigDecimal precio, int stock, Categoria categoria,
                                       boolean activo, LocalDateTime fechaCreacion) {
        return new Producto(id, codigo, nombre, descripcion, precio, stock, categoria,
                activo, fechaCreacion);
    }

    /** Regla de negocio: el nuevo precio debe ser válido. */
    public void cambiarPrecio(BigDecimal nuevoPrecio) {
        this.precio = validarPrecio(nuevoPrecio);
    }

    private static String validarCodigo(String codigo) {
        if (codigo == null || !PATRON_CODIGO.matcher(codigo).matches()) {
            throw new IllegalArgumentException(
                    "El código debe tener el formato AAA-0000 (3 letras mayúsculas, guion y 4 dígitos)");
        }
        return codigo;
    }

    private static String validarNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre es obligatorio");
        }
        String limpio = nombre.trim();
        if (limpio.length() > 100) {
            throw new IllegalArgumentException("El nombre no puede superar 100 caracteres");
        }
        return limpio;
    }

    private static String validarDescripcion(String descripcion) {
        if (descripcion == null || descripcion.isBlank()) {
            return null;
        }
        if (descripcion.length() > 255) {
            throw new IllegalArgumentException("La descripción no puede superar 255 caracteres");
        }
        return descripcion.trim();
    }

    private static BigDecimal validarPrecio(BigDecimal precio) {
        if (precio == null) {
            throw new PrecioInvalidoException("El precio es obligatorio");
        }
        if (precio.signum() <= 0) {
            throw new PrecioInvalidoException("El precio debe ser mayor que cero");
        }
        if (precio.stripTrailingZeros().scale() > 2) {
            throw new PrecioInvalidoException("El precio admite como máximo 2 decimales");
        }
        if (precio.compareTo(PRECIO_MAXIMO) > 0) {
            throw new PrecioInvalidoException("El precio no puede superar " + PRECIO_MAXIMO);
        }
        return precio.setScale(2, RoundingMode.UNNECESSARY);
    }

    private static int validarStock(int stock) {
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        return stock;
    }

    public Long getId() { return id; }
    public String getCodigo() { return codigo; }
    public String getNombre() { return nombre; }
    public String getDescripcion() { return descripcion; }
    public BigDecimal getPrecio() { return precio; }
    public int getStock() { return stock; }
    public Categoria getCategoria() { return categoria; }
    public boolean isActivo() { return activo; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
}
