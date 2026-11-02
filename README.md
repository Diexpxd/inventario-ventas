# Inventario y Ventas

Evidencia final de Programación Avanzada en Java (Tecmilenio). Es la implementación del diseño del Avance 1: un sistema web de inventario y ventas con Spring MVC, una interfaz con JSP y una API REST que responde en JSON y XML.

- Empresa: Papelería y Novedades Cumbres (Monterrey, N. L.)
- Repositorio: https://github.com/Diexpxd/inventario-ventas

## Requisitos

- JDK 8 o superior (se compila para Java 1.8; se probó con JDK 11)
- Maven 3.6 o superior
- Opcional: MySQL 8 (por defecto la app usa H2 en memoria, no hay que instalar nada)

## Cómo correrlo

```
mvn jetty:run
```

Abrir <http://127.0.0.1:8080/inventario>. También se puede generar el WAR con `mvn package` (queda en `target/inventario-ventas.war`) y desplegarlo en cualquier contenedor de servlets 3.1 (Tomcat 8/9, Jetty, etc.).

Usuarios de prueba que crea la aplicación al arrancar:

| Usuario | Contraseña | Rol |
|---|---|---|
| admin | admin123 | ADMIN |
| vendedor | vendedor123 | VENDEDOR |
| almacen | almacen123 | ALMACENISTA |

Estas contraseñas son solo para pruebas locales; se guardan con BCrypt.

## Base de datos

Por defecto: H2 en memoria en modo MySQL, con `schema.sql` y `datos.sql` (en `src/main/resources/db`) cargados al arrancar.

Para usar MySQL:

1. Crear la base: `CREATE DATABASE inventario_ventas CHARACTER SET utf8mb4;`
2. Ejecutar `schema.sql` y `datos.sql` en esa base.
3. Arrancar con:

```
mvn jetty:run -Ddb.url="jdbc:mysql://localhost:3306/inventario_ventas?useSSL=false&serverTimezone=America/Mexico_City" -Ddb.driver=com.mysql.cj.jdbc.Driver -Ddb.user=root -Ddb.password=TU_CONTRASENA -Ddb.init=false
```

## Estructura

```
src/main/java/mx/tecmilenio/inventario
  modelo/        entidades y objetos de la API (Producto, Venta, Recurso, Coleccion...)
  dto/           objetos que llegan en las peticiones
  repositorio/   acceso a datos con JdbcTemplate
  servicio/      reglas de negocio y transacciones
  controlador/   controladores MVC con JSP
  api/           controladores REST (JSON y XML) y enlaces hypermedia
  seguridad/     interceptor de autenticación y permisos por rol
  excepcion/     excepciones de negocio y la respuesta de error
  config/        configuración de Spring (Java config)
src/main/webapp  vistas JSP y CSS
src/test         pruebas con MockMvc
```

Arquitectura: MVC en la capa web (Controller, modelo, vistas JSP) y CSR en el resto (Controller → Service → Repository).

## API REST

Base: `/inventario/api`. Autenticación HTTP Basic. El formato se elige con el encabezado `Accept` (`application/json` o `application/xml`) o con `?formato=json|xml`; por defecto responde JSON.

| Recurso | Métodos |
|---|---|
| `/api` | GET (índice con enlaces) |
| `/api/productos`, `/api/productos/{id}` | GET, POST, PUT, DELETE |
| `/api/categorias` | GET, POST |
| `/api/movimientos`, `/api/movimientos/{id}` | GET, POST |
| `/api/inventario/alertas` | GET |
| `/api/ventas`, `/api/ventas/{id}` | GET, POST, DELETE (cancela la venta) |
| `/api/usuarios`, `/api/usuarios/{id}` | GET, POST, PUT, DELETE (desactiva) |

Cada respuesta incluye `links` con las acciones disponibles según el rol y el estado del recurso. Los errores regresan un objeto `error` (status, error, mensaje, ruta y fecha) en el mismo formato que se pidió.

Ejemplo:

```
curl -u admin:admin123 http://127.0.0.1:8080/inventario/api/productos/1
curl -u admin:admin123 -H "Accept: application/xml" http://127.0.0.1:8080/inventario/api/productos/1
```

## Pruebas

```
mvn test
```

Son 27 pruebas con MockMvc sobre la API (autenticación, permisos por rol, JSON y XML, errores, ciclo de productos, ventas con descuento de stock y cancelación).

## Documentación del código

```
mvn javadoc:javadoc
```

Genera el Javadoc en `target/site/apidocs/index.html`. En el zip de la entrega ya viene generado en `docs/apidocs/index.html`.

## Ramas en Git

- `master`: versión final (con etiqueta `v1.0.0`)
- `develop`: integración
- `feature/*`: una rama por parte del sistema, mezcladas a `develop` sin borrarlas
