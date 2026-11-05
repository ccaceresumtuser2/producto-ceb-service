package com.ejemplo.producto.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Información general que se muestra en Swagger UI. */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI productoOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Producto Service API")
                .description("""
                        CRUD de productos con arquitectura cebolla (Spring Boot + PostgreSQL).

                        **Formato de respuesta**: todas las respuestas, de éxito y de error, usan el mismo formato:

                        | Campo | Descripción |
                        |---|---|
                        | `message` | Mensaje para el cliente |
                        | `timestamp` | Fecha y hora en que se generó la respuesta |
                        | `data` | Contenido. `null` si no hay nada que devolver; en errores de validación trae el detalle por campo |
                        | `status` | Código HTTP (igual al de la respuesta) |

                        Códigos: 200 OK · 201 creado · 400 datos/precio inválidos · 404 no existe · 405 método no permitido · 409 código duplicado · 500 error interno.
                        """)
                .version("1.0.0"));
    }
}
