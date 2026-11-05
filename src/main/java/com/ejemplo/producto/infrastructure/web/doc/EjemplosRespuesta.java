package com.ejemplo.producto.infrastructure.web.doc;

/**
 * Ejemplos de respuesta que muestra Swagger UI.
 * Todos siguen el formato RespuestaApi: { message, timestamp, data, status }.
 */
public final class EjemplosRespuesta {

    private EjemplosRespuesta() {
    }

    private static final String PRODUCTO = """
            {
                "id": 5,
                "codigo": "TEC-0010",
                "nombre": "Teclado mecánico Redragon",
                "descripcion": "Teclado RGB switches rojos, español",
                "precio": 189900.00,
                "stock": 15,
                "categoria": "TECNOLOGIA",
                "activo": true,
                "fechaCreacion": "2026-11-04T16:40:00.000"
              }""";

    private static final String PRODUCTO_PRECIO_NUEVO = """
            {
                "id": 5,
                "codigo": "TEC-0010",
                "nombre": "Teclado mecánico Redragon",
                "descripcion": "Teclado RGB switches rojos, español",
                "precio": 159900.00,
                "stock": 15,
                "categoria": "TECNOLOGIA",
                "activo": true,
                "fechaCreacion": "2026-11-04T16:40:00.000"
              }""";

    // --- Éxito ---

    public static final String CREADO = """
            {
              "message": "Producto creado correctamente",
              "timestamp": "2026-11-04T16:40:00.123",
              "data": """ + PRODUCTO + """
            ,
              "status": 201
            }""";

    public static final String ENCONTRADO = """
            {
              "message": "Producto encontrado",
              "timestamp": "2026-11-04T16:40:00.123",
              "data": """ + PRODUCTO + """
            ,
              "status": 200
            }""";

    public static final String LISTA = """
            {
              "message": "Se encontraron 1 productos",
              "timestamp": "2026-11-04T16:40:00.123",
              "data": [ """ + PRODUCTO + """
             ],
              "status": 200
            }""";

    public static final String PRECIO_ACTUALIZADO = """
            {
              "message": "Precio actualizado correctamente",
              "timestamp": "2026-11-04T16:40:00.123",
              "data": """ + PRODUCTO_PRECIO_NUEVO + """
            ,
              "status": 200
            }""";

    public static final String ELIMINADO = """
            {
              "message": "Producto eliminado correctamente",
              "timestamp": "2026-11-04T16:40:00.123",
              "data": null,
              "status": 200
            }""";

    // --- Errores ---

    public static final String VALIDACION = """
            {
              "message": "Datos inválidos",
              "timestamp": "2026-11-04T16:40:00.123",
              "data": {
                "codigo": "debe tener el formato AAA-0000",
                "nombre": "no debe estar vacío"
              },
              "status": 400
            }""";

    public static final String PRECIO_INVALIDO = """
            {
              "message": "El precio debe ser mayor que cero",
              "timestamp": "2026-11-04T16:40:00.123",
              "data": null,
              "status": 400
            }""";

    public static final String NO_ENCONTRADO = """
            {
              "message": "No existe un producto con id 999",
              "timestamp": "2026-11-04T16:40:00.123",
              "data": null,
              "status": 404
            }""";

    public static final String DUPLICADO = """
            {
              "message": "Ya existe un producto con ese código o los datos violan una restricción",
              "timestamp": "2026-11-04T16:40:00.123",
              "data": null,
              "status": 409
            }""";

    public static final String ERROR_INTERNO = """
            {
              "message": "Error interno del servidor",
              "timestamp": "2026-11-04T16:40:00.123",
              "data": null,
              "status": 500
            }""";
}
