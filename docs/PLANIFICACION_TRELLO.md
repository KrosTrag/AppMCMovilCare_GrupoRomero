# Plan de trabajo para Trello

Esta guía es una plantilla para crear evidencia real de planificación; no afirma que exista un tablero. El proyecto fue realizado por una sola persona, Anthony Alexander Romero Bracamonte, por lo que todas las tarjetas se asignan a ese integrante y no se simula trabajo en equipo.

## Crear el tablero

1. Inicia sesión o crea una cuenta en [Trello](https://trello.com/).
2. Crea un tablero llamado `MCmovil Care - EFT Parcial 4`.
3. Agrega las listas `Pendiente`, `En curso`, `En revisión` y `Hecho`.
4. Crea las tarjetas de la tabla y asígnalas a Anthony; mueve cada tarjeta según el trabajo que realmente hayas completado.
5. En las tarjetas terminadas, adjunta el enlace del commit relacionado, una descripción breve y una captura real de la app o de las pruebas.
6. Guarda capturas del tablero completo y de algunas tarjetas abiertas para adjuntarlas a la entrega académica.

## Tarjetas sugeridas

| Tarjeta | Criterio de aceptación | Evidencia para adjuntar |
|---|---|---|
| Implementar registro y navegación Android | El formulario valida datos y registra una orden en el servicio de tickets. | Captura del flujo y referencia al commit. |
| Implementar consulta y edición del cliente | El cliente encuentra su orden con ID/correo y actualiza el problema. | Captura antes/después y referencia al commit. |
| Implementar acceso y panel de administrador | La sesión permite listar, cambiar estados y eliminar órdenes; logout invalida la sesión. | Capturas de sesión/panel y referencia al commit. |
| Desarrollar microservicio de tickets | Endpoints CRUD funcionales y base H2 exclusiva de tickets. | Enlace de pruebas y referencia al commit. |
| Desarrollar microservicio de autenticación | Login, sesiones, expiración y base H2 independiente. | Enlace de pruebas y referencia al commit. |
| Integrar API externa | El catálogo DummyJSON aparece en la interfaz sin bloquear los flujos propios si falla. | Captura de catálogo y referencia al commit. |
| Probar lógica Android | Tests de ViewModel/repositorio y validación; `:app:check` aplica cobertura mínima del 80%. | Resultado de Gradle y reporte JaCoCo. |
| Preparar APK release firmado | APK verificable en `app/build/outputs/apk/release/app-release.apk`; almacén privado no se adjunta al tablero público. | Captura de verificación de firma, nunca la clave ni contraseña. |
| Completar documentación EFT | README y documento técnico describen contexto, arquitectura, APIs, pruebas y despliegue. | Enlaces a los documentos. |

## Evidencia pendiente

La tabla solo propone trabajo para registrar. Para acreditar Trello, Anthony debe crear el tablero, registrar/mover las tarjetas según su estado real y adjuntar las capturas o el enlace del tablero. No publiques contraseñas, llaves `.jks` ni datos personales de terceros.