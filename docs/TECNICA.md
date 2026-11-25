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

- `TicketController`: alta y operaciones de propietario.
- `AdminTicketController`: lectura completa, modificación de estado y eliminación; cada operación requiere un token válido.
- `AdminAuthController`/`AdminAuthService`: autentican al administrador configurado y emiten tokens aleatorios de sesión.
- `TicketService`: genera ID, verifica el correo propietario, valida transiciones de estado y delega persistencia en JPA.
- H2 mantiene los datos en `backend/data/`; durante el desarrollo se habilita consola local en `/h2-console`.

## Contratos de datos y permisos

- La creación requiere nombre, correo válido, modelo, número de serie y descripción no vacía.
- El cliente solo puede consultar y editar `diagnostico`; debe entregar el mismo correo registrado junto con el ID.
- La lista completa, el cambio de estado y el borrado se protegen con `Authorization: Bearer <token>`.
- Estados permitidos: `Ingresado`, `En Diagnóstico`, `Esperando Autorización`, `Reparado` y `Entregado`.
- Las respuestas de error usan estados HTTP: `400` entrada inválida, `401` sesión inválida, `403` correo no propietario y `404` solicitud inexistente.

## Pruebas y cobertura

Las pruebas Android cubren datos completos, campos obligatorios y formato de correo (`TicketFormValidatorTest`). Las pruebas backend cubren creación, consulta, actualización autorizada/no autorizada, IDs inexistentes, estados, eliminación y login/logout. `:backend:check` exige al menos 80% de cobertura de líneas en `TicketService` y `AdminAuthService`; el informe se genera en `backend/build/reports/jacoco/test/html/`.

## Configuración de despliegue

- Android usa `apiBaseUrl` Gradle property; por defecto `http://10.0.2.2:8080/` para emulador.
- Backend acepta `PORT`, `ADMIN_EMAIL` y `ADMIN_PASSWORD` desde entorno.
- La clave privada de release y las contraseñas deben almacenarse fuera del repositorio. La autenticación de demostración utiliza sesiones en memoria; al reiniciar el backend se invalidan. En despliegue real se recomienda HTTPS, contraseña hasheada, límites de intentos, expiración de tokens y una solución de identidad más robusta que ID+correo.
- GitHub/Trello requieren acciones y evidencia del equipo, no reproducibles desde esta carpeta sin el repositorio remoto y sus accesos.