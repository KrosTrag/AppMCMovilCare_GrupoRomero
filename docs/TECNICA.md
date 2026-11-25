# Documentación técnica — MCmovil Care

## Propósito y contexto EFT

MCmovil Care representa un servicio técnico de equipos TI: el cliente registra el equipo y sus síntomas; el personal autorizado administra el diagnóstico y el estado. El objetivo del EFT es demostrar una aplicación funcional que integra frontend Android, persistencia, microservicios, API externa y pruebas automatizadas.

## Componentes

### Android

- `MainActivity` configura rutas para inicio, registro, edición de solicitud, autenticación administrativa y panel.
- `UsuarioViewModel` valida formularios, coordina operaciones de red y sincroniza los resultados recibidos con Room.
- `TicketRepository` y `TicketApiService` encapsulan Retrofit para el backend; `ExternalCatalogApi` consume DummyJSON por separado.
- `OrdenTrabajoEntity`/`OrdenDao` conservan localmente solicitudes consultadas/registradas, y no reemplazan a H2 como fuente remota.

### Spring Boot

- `backend/` ejecuta el microservicio de tickets en el puerto `8080`. `TicketController` permite crear, consultar y editar solicitudes; `AdminTicketController` gestiona el panel y valida la sesión consultando al servicio de identidad.
- `TicketService`: genera ID, verifica el correo propietario, valida transiciones de estado y delega persistencia en JPA.
- La base H2 exclusiva de tickets se guarda en `backend/data/tickets`; no almacena credenciales ni sesiones administrativas.
- `auth-service/` ejecuta el microservicio de identidad en el puerto `8081`. Sus endpoints públicos gestionan login/logout; `/internal/sessions/validate` permite al backend comprobar tokens.
- La base H2 exclusiva de identidad se guarda en `auth-service/data/authentication` y contiene usuarios y sesiones. Las contraseñas se almacenan con BCrypt y las sesiones expiran según `ADMIN_SESSION_HOURS`.
- El backend mantiene `/api/auth/admin` como fachada compatible para Android y reenvía esas solicitudes al microservicio de identidad configurado por `AUTH_SERVICE_URL`.

## Contratos de datos y permisos

- La creación requiere nombre, correo válido, modelo, número de serie y descripción no vacía.
- El cliente solo puede consultar y editar `diagnostico`; debe entregar el mismo correo registrado junto con el ID.
- La lista completa, el cambio de estado y el borrado se protegen con `Authorization: Bearer <token>`.
- Estados permitidos: `Ingresado`, `En Diagnóstico`, `Esperando Autorización`, `Reparado` y `Entregado`.
- Las respuestas de error usan estados HTTP: `400` entrada inválida, `401` sesión inválida, `403` correo no propietario y `404` solicitud inexistente.

## Pruebas y cobertura

Las pruebas Android cubren validación de formularios, delegación del repositorio para flujos de cliente/administrador/API externa y conversión del DTO remoto a Room. `:app:check` impone 80% de cobertura de líneas al validador, repositorio y mapeo del ticket; el informe JaCoCo queda en `app/build/reports/jacoco/jacocoDebugUnitTestReport/html/index.html`. Las pruebas del microservicio de tickets cubren creación, consulta, edición autorizada/no autorizada, estados, eliminación y comunicación con identidad. Las pruebas de `auth-service` verifican credenciales, persistencia de sesiones, validación, expiración e invalidación al cerrar sesión. `:backend:check` y `:auth-service:check` aplican un umbral de cobertura de líneas del 80% a sus servicios principales.

## Configuración de despliegue

- Android usa `apiBaseUrl` Gradle property; por defecto `http://10.0.2.2:8080/` para emulador.
- Tickets acepta `PORT` y `AUTH_SERVICE_URL`; identidad acepta `AUTH_PORT`, `ADMIN_EMAIL`, `ADMIN_PASSWORD` y `ADMIN_SESSION_HOURS`. Las credenciales administrativas solo se configuran en identidad y no se comparten con el servicio de tickets.
- Ejecuta `:auth-service:bootRun` y `:backend:bootRun` en terminales separados; para la app en un emulador Android, configura `AUTH_SERVICE_URL=http://localhost:8081` en el proceso backend.
- Las bases son ficheros separados y están excluidas de Git. La clave privada de release y las contraseñas también deben mantenerse fuera del repositorio. En despliegues reales se recomienda HTTPS, gestión segura de secretos, límites de intentos y control del acceso de red al endpoint interno de validación.
- GitHub/Trello requieren acciones y evidencia del equipo, no reproducibles desde esta carpeta sin el repositorio remoto y sus accesos.