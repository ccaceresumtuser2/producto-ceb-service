# producto-service (arquitectura cebolla)

API REST de productos construida con **arquitectura cebolla** (*Onion Architecture*, Jeffrey Palermo) usando Spring Boot y PostgreSQL.

Es el mismo servicio que la versión hexagonal (`hexagonal/app-productos`): mismos endpoints, mismas reglas de negocio, misma base de datos. Solo cambia **cómo se organiza el código**.

## Arquitectura

Diagrama completo en [`docs/arquitectura.drawio`](docs/arquitectura.drawio). Se abre en [app.diagrams.net](https://app.diagrams.net) (*File → Open from → Device*) o en VS Code con la extensión **Draw.io Integration** (`hediet.vscode-drawio`).

```
                ┌──────────────────── Infraestructura ────────────────────┐
                │  web/ (Controller, DTOs, errores)   persistence/ (JPA)  │
                │  config/ (BeanConfig, OpenApiConfig)                    │
                │   ┌────────────── Servicios de aplicación ───────────┐  │
                │   │  ICrearProducto ◄── CrearProducto                │  │
                │   │  IObtenerProducto ◄── ObtenerProducto            │  │
                │   │  ICambiarPrecioProducto ◄── CambiarPrecioProducto│  │
                │   │  IEliminarProducto ◄── EliminarProducto          │  │
                │   │   ┌────────── Servicios de dominio ──────────┐   │  │
                │   │   │  IProductoRepository                     │   │  │
                │   │   │   ┌────── Modelo de dominio ──────┐      │   │  │
                │   │   │   │  Producto, Categoria,         │ ◄────┼───┼──┤
                │   │   │   │  excepciones del dominio      │      │   │  │
                │   │   │   └───────────────────────────────┘      │   │  │
                │   │   └──────────────────────────────────────────┘   │  │
                │   └──────────────────────────────────────────────────┘  │
                └─────────────────────────────────────────────────────────┘
                     Las dependencias siempre apuntan hacia el centro
```

### Estructura de carpetas

```
src/main/java/com/ejemplo/producto/
├─ domain/                         ← núcleo (Java puro, sin Spring/JPA/HTTP)
│  ├─ model/
│  │  ├─ Producto                  ← datos + reglas de negocio
│  │  └─ Categoria
│  ├─ exception/
│  │  ├─ PrecioInvalidoException
│  │  └─ ProductoNoEncontradoException
│  └─ services/
│     └─ IProductoRepository       ← contrato que necesita el dominio
├─ application/                    ← servicios de aplicación (casos de uso)
│  ├─ ICrearProducto            ← interfaz (lo que usa el controlador)
│  ├─ CrearProducto             ← implementación
│  ├─ CrearProductoCommand
│  ├─ IObtenerProducto
│  ├─ ObtenerProducto
│  ├─ ICambiarPrecioProducto
│  ├─ CambiarPrecioProducto
│  ├─ IEliminarProducto
│  └─ EliminarProducto
└─ infrastructure/                 ← detalles técnicos (anillo externo)
   ├─ web/
   │  ├─ ProductoController
   │  ├─ GlobalExceptionHandler
   │  ├─ dto/  (requests, ProductoResponse, RespuestaApi)
   │  └─ doc/  (schemas y ejemplos de Swagger)
   ├─ persistence/
   │  ├─ ProductoRepositoryJpa     ← implementa IProductoRepository
   │  ├─ ProductoEntity
   │  ├─ ProductoJpaRepository
   │  └─ ProductoMapper
   └─ config/
      ├─ BeanConfig
      └─ OpenApiConfig
```

| Capa | Paquete | Qué contiene | Puede depender de |
|---|---|---|---|
| Modelo de dominio | `domain.model`, `domain.exception` | Entidades con sus reglas de negocio | Nadie |
| Servicios de dominio | `domain.services` | Contratos que necesita el dominio (`IProductoRepository`) | Modelo |
| Servicios de aplicación | `application` | Interfaces `I*Producto` y sus implementaciones: coordinan cada acción (buscar → aplicar regla → guardar) | Dominio |
| Infraestructura | `infrastructure` | HTTP, JPA, Swagger, configuración de Spring | Todas las internas |

Regla de dependencia: una capa **solo conoce a las que están más adentro**. `domain` no importa nada de `application` ni de `infrastructure`, y `application` no importa nada de `infrastructure`. La flecha del diagrama muestra que la infraestructura puede usar directamente el modelo de dominio (por ejemplo, `ProductoMapper` y `ProductoResponse` usan `Producto`).

### Flujo de una petición

```
PATCH /api/productos/1/precio
  └─► ProductoController            (infrastructure/web)
        └─► ICambiarPrecioProducto  (application)  ──► implementada por CambiarPrecioProducto
              ├─► IProductoRepository.buscarPorId   (domain/services)  ──► ProductoRepositoryJpa (infrastructure/persistence)
              ├─► producto.cambiarPrecio(...)       (domain/model: aplica la regla)
              └─► IProductoRepository.guardar       (domain/services)  ──► ProductoRepositoryJpa
```

### Inversión de dependencias

Cada clase depende de **interfaces**, nunca de clases concretas de otra capa. Hay dos puntos de inversión:

```
ProductoController ──► ICambiarPrecioProducto ◄── CambiarPrecioProducto ──► IProductoRepository ◄── ProductoRepositoryJpa
 (infrastructure)          (application)            (application)           (domain/services)      (infrastructure)
```

1. **Controlador → servicio de aplicación.** `ProductoController` recibe `ICrearProducto`, `IObtenerProducto`, `ICambiarPrecioProducto` e `IEliminarProducto`. No sabe qué clase las implementa, así que se puede cambiar la implementación (o envolverla en un decorador de logging o transacciones) sin tocar el controlador.
2. **Servicio de aplicación → repositorio.** `IProductoRepository` se **declara** en el dominio y se **implementa** en la infraestructura. El servicio de aplicación nunca sabe que existe JPA: cambiar PostgreSQL por MongoDB o por una lista en memoria es escribir otra clase que implemente `IProductoRepository`, sin tocar `domain` ni `application`.

`BeanConfig` (en infraestructura) es el **único** lugar que conoce las clases concretas: crea cada servicio con `new`, le inyecta el repositorio y lo registra como bean con el tipo de su interfaz:

```java
@Bean
public ICambiarPrecioProducto cambiarPrecioProducto(IProductoRepository repositorio) {
    return new CambiarPrecioProducto(repositorio);
}
```

Así `domain` y `application` no llevan anotaciones de Spring y se prueban con `new CambiarPrecioProducto(repositorioSimulado)`. En `ProductoControllerTest` se simulan las interfaces `I*Producto` con `@MockitoBean`.

### Principios SOLID

| Principio | Cómo se aplica |
|---|---|
| **S**: Responsabilidad única | `Producto` (reglas), un servicio de aplicación por acción, `ProductoController` (HTTP), `GlobalExceptionHandler` (errores → HTTP), `ProductoMapper` (dominio ↔ entidad), `ProductoRepositoryJpa` (base de datos). |
| **O**: Abierto/cerrado | Una acción nueva es una interfaz y un servicio nuevos, sin tocar los existentes. Otra base de datos es otra implementación de `IProductoRepository`. |
| **L**: Sustitución de Liskov | Cualquier implementación de una interfaz reemplaza a otra: las pruebas usan mocks de `IProductoRepository` y de las `I*Producto`. |
| **I**: Segregación de interfaces | Cuatro interfaces pequeñas (`ICrearProducto`, `IObtenerProducto`, `ICambiarPrecioProducto`, `IEliminarProducto`) en vez de un `IProductoService` con todo. |
| **D**: Inversión de dependencias | El controlador depende de `I*Producto` y los servicios dependen de `IProductoRepository`. Solo `BeanConfig` conoce las clases concretas. |

## Hexagonal vs. cebolla: qué cambió

| Hexagonal (`app-productos`) | Cebolla (`app-productos-ceb`) |
|---|---|
| `core/model/` | `domain/model/` |
| `core/exception/` | `domain/exception/` |
| `core/ports/out/` `Buscar`/`Guardar`/`EliminarProductoPortOut` | `domain/services/IProductoRepository` (un solo contrato) |
| `core/ports/in/*PortIn` (interfaces de entrada) | `application/I*Producto` (`ICrearProducto`, `IObtenerProducto`, `ICambiarPrecioProducto`, `IEliminarProducto`) |
| `core/ports/in/CrearProductoCommand` | `application/CrearProductoCommand` |
| `core/usecase/` | `application/` |
| `adapters/in/web/` | `infrastructure/web/` |
| `adapters/out/persistence/ProductoRepositoryAdapter` | `infrastructure/persistence/ProductoRepositoryJpa` |
| `config/` | `infrastructure/config/` |

Las dos arquitecturas comparten la idea central: **el dominio no depende de la infraestructura**. La diferencia está en el énfasis:

- **Hexagonal** distingue *entrada* (puertos que el exterior invoca) de *salida* (puertos que el núcleo necesita), y los dos lados se conectan con adaptadores.
- **Cebolla** organiza el código en **anillos concéntricos** (modelo → servicios de dominio → servicios de aplicación → infraestructura). No hay un lado de entrada y otro de salida: la web y la base de datos están en el mismo anillo externo.

Las interfaces `I*Producto` cumplen el mismo papel que los `PortIn` de la hexagonal, pero aquí no se llaman "puertos": son el contrato público del anillo de servicios de aplicación. El controlador (infraestructura) depende de ese contrato, que está en un anillo interno, así que se respeta la regla de dependencia y además el principio de inversión de dependencias.

## Lógica de negocio

La lógica de negocio vive en el dominio y se reparte según una pregunta: **¿la regla necesita algo de fuera?**

| Tipo de regla | Dónde va | Ejemplo |
|---|---|---|
| Solo necesita los datos del producto | **Modelo** (`Producto`) | El precio debe ser mayor que 0 |
| Necesita consultar o guardar algo | **Servicio de aplicación** (usando `IProductoRepository`) | No se puede eliminar un producto que no existe |

Reglas en [`Producto`](src/main/java/com/ejemplo/producto/domain/model/Producto.java):

- Código con formato `AAA-0000`.
- Nombre obligatorio, máximo 100 caracteres.
- Descripción de máximo 255 caracteres.
- Precio obligatorio, mayor que 0 y con máximo 2 decimales (`PrecioInvalidoException`).
- Stock no negativo.
- Un producto nuevo empieza activo y con la fecha actual.

Todas se validan en el constructor privado, así que **no puede existir un `Producto` inválido**.

```java
// CambiarPrecioProducto (application)
Producto producto = repositorio.buscarPorId(id)                    // 1. buscar
        .orElseThrow(() -> new ProductoNoEncontradoException(id)); //    si no existe → error
producto.cambiarPrecio(nuevoPrecio);                               // 2. el modelo aplica la regla
return repositorio.guardar(producto);                              // 3. guardar
```

## Tecnologías

| | Versión |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.7 |
| PostgreSQL | 18 |
| Flyway | migraciones de base de datos |
| springdoc-openapi | 2.8.9 (Swagger UI) |
| JUnit 5 + Mockito + MockMvc | pruebas |
| H2 | base de datos en memoria para las pruebas |

## Requisitos

- JDK 21
- Maven 3.9+ (o el wrapper `./mvnw`)
- PostgreSQL corriendo en `localhost:5432` con la base de datos `topicos_db` (solo para ejecutar la aplicación; las pruebas usan H2)

Configuración en [`application.yml`](src/main/resources/application.yml). La tabla `productos` la crea Flyway (`V1__crear_tabla_productos.sql`) al arrancar.

## Ejecutar

```bash
mvn spring-boot:run
```

La aplicación queda en `http://localhost:8090`, con Swagger UI en http://localhost:8090/swagger-ui.html.

## Endpoints

Base: `/api/productos`

| Método | Ruta | Descripción | Respuestas |
|---|---|---|---|
| `POST` | `/api/productos` | Crear un producto | 201, 400, 409 |
| `GET` | `/api/productos` | Listar productos | 200 |
| `GET` | `/api/productos/{id}` | Obtener un producto | 200, 404 |
| `PATCH` | `/api/productos/{id}/precio` | Cambiar el precio | 200, 400, 404 |
| `DELETE` | `/api/productos/{id}` | Eliminar un producto | 200, 404 |

Todas las respuestas usan el formato [`RespuestaApi`](src/main/java/com/ejemplo/producto/infrastructure/web/dto/RespuestaApi.java): `{ message, timestamp, data, status }`.

## Pruebas

```bash
mvn test
```

| Prueba | Capa | Qué verifica | Necesita |
|---|---|---|---|
| `ProductoTest` | Modelo de dominio | Reglas de negocio | Nada (Java puro) |
| `CambiarPrecioProductoTest` | Servicios de aplicación | Servicio con `IProductoRepository` simulado (Mockito) | Nada |
| `ProductoControllerTest` | Infraestructura (web) | Endpoints y códigos HTTP (MockMvc) con las interfaces `I*Producto` simuladas | Nada |
| `ProductoRepositoryJpaTest` | Infraestructura (persistencia) | Acceso real a la base de datos con JPA y Flyway | Nada (H2 en memoria) |

Las pruebas **no necesitan PostgreSQL**: usan **H2 en memoria** en modo PostgreSQL, configurada en [`src/test/resources/application.yml`](src/test/resources/application.yml). Ese archivo reemplaza al `application.yml` principal solo durante `mvn test`; la aplicación sigue usando PostgreSQL.

- Flyway ejecuta la misma migración `V1__crear_tabla_productos.sql` sobre H2, así que la prueba verifica también el esquema real (índice único, `CHECK` de precio, stock y categoría).
- `ddl-auto: validate` comprueba que `ProductoEntity` coincide con la tabla creada por Flyway.
- La base se crea al iniciar las pruebas y desaparece al terminar; cada prueba además revierte su transacción.

> H2 imita a PostgreSQL pero no es idéntico. Para probar algo propio de PostgreSQL (tipos `jsonb`, funciones específicas), la opción es Testcontainers con una imagen real de PostgreSQL.

### Integración continua (GitHub Actions)

[`.github/workflows/ci.yml`](.github/workflows/ci.yml) ejecuta `./mvnw test` en cada *push* a `main`/`master`, en cada *pull request* y a mano (*Run workflow*).

- Usa JDK 21 (Temurin) con caché de dependencias de Maven.
- No levanta ninguna base de datos: todas las pruebas, incluida `ProductoRepositoryJpaTest`, corren con H2 en memoria.
- Sube los reportes de `target/surefire-reports/` como artefacto `reportes-surefire`, incluso si alguna prueba falla.
