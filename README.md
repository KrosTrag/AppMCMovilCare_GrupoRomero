# MCmovil Care

Aplicación móvil de servicio técnico para ingresar solicitudes de reparación, consultar y corregir el problema reportado, y gestionar las órdenes desde un perfil administrativo. El proyecto está alineado con el EFT como solución a la necesidad de trazabilidad y atención de equipos tecnológicos.

## Integrantes

Completar con los nombres oficiales del equipo antes de entregar.

## Funcionalidades

- Registro de solicitudes de diagnóstico desde Android con Kotlin y Jetpack Compose.
- Persistencia remota de órdenes mediante microservicio Spring Boot y base de datos H2; Room mantiene una copia local de las solicitudes consultadas.
- Consulta y edición de la descripción del problema por el cliente, comprobando el ID de la orden y el correo asociado.
- Inicio de sesión exclusivo para el administrador. El panel permite listar solicitudes, avanzar su estado y eliminarlas.
- Consumo Retrofit de catálogo externo de teléfonos desde DummyJSON; si la API externa no responde, las demás funciones siguen disponibles.
- Pruebas unitarias en la validación del formulario Android y en servicios backend, con umbral JaCoCo del 80% para `TicketService` y `AdminAuthService`.
- Configuración Gradle para generar un APK release firmado usando variables de entorno y un almacén de claves local.

## Arquitectura

- `app/`: cliente Android Kotlin/Compose, Retrofit, Room y navegación.
- `backend/`: microservicio Spring Boot REST, JPA y H2.
- Contexto EFT: recepción de equipos y seguimiento de problemas de hardware/software, manteniendo separados los permisos del administrador y del cliente.

## APIs y endpoints

Microservicio propio (base URL Android por defecto: `http://10.0.2.2:8080/`):

| Método | Endpoint | Acceso | Propósito |
|---|---|---|---|
| `POST` | `/api/tickets` | Público | Registrar solicitud |
| `GET` | `/api/tickets/{id}?email={correo}` | Cliente | Consultar solicitud con ID y correo |
| `PUT` | `/api/tickets/{id}?email={correo}` | Cliente | Modificar solo la descripción del problema |
| `POST` | `/api/auth/admin` | Público | Iniciar sesión y emitir token de administrador |
| `POST` | `/api/auth/admin/logout` | Administrador | Invalidar token de sesión |
| `GET` | `/api/admin/tickets` | Administrador | Listar todas las órdenes |
| `PATCH` | `/api/admin/tickets/{id}/status` | Administrador | Cambiar estado |
| `DELETE` | `/api/admin/tickets/{id}` | Administrador | Eliminar solicitud |

El backend usa H2 persistente en `backend/data/`. El catálogo externo se consume desde `https://dummyjson.com/products/category/smartphones?limit=5`.

## Requisitos y ejecución local

Se requiere Android Studio/SDK Android y Java 17 o posterior. El backend se ejecuta en el puerto `8080`; la configuración de administrador puede sobrescribirse con `ADMIN_EMAIL` y `ADMIN_PASSWORD`.

```powershell
$env:ADMIN_EMAIL = 'admin@tu-dominio.cl'
$env:ADMIN_PASSWORD = 'DefineUnaClaveLocalSegura'
.\gradlew.bat ':backend:bootRun'
```

En un segundo terminal, abre el proyecto en Android Studio y ejecuta la app en un emulador Android. El host `10.0.2.2` apunta desde el emulador al computador donde corre Spring Boot. Para un dispositivo físico, compila con la IP LAN del computador:

```powershell
.\gradlew.bat ':app:installDebug' '-PapiBaseUrl=http://192.168.1.20:8080/'
```

Credenciales de desarrollo por defecto: `admin@mcmovilcare.local` / `AdminLocal2026!`. Cámbialas mediante variables de entorno; no uses HTTP ni estas credenciales en producción. La edición de cliente comprueba ID y correo como verificación del propietario, no sustituye un mecanismo de identidad/OTP de producción.

## Pruebas

```powershell
.\gradlew.bat ':app:testDebugUnitTest'
.\gradlew.bat ':backend:check'
```

El segundo comando ejecuta las pruebas backend y falla si la cobertura de líneas de `TicketService` o `AdminAuthService` queda bajo 80%. Informe JaCoCo: `backend/build/reports/jacoco/test/html/index.html`.

## APK release firmado

La llave de firma es privada: se mantiene local y está excluida de Git. No subas el `.jks`, las contraseñas ni `local.properties` al repositorio público. Genera un almacén con una contraseña propia y exporta sus datos al entorno antes de compilar:

```powershell
New-Item -ItemType Directory -Force 'app\signing'
keytool -genkeypair -v -keystore 'app\signing\mcmovilcare-release.jks' -alias 'mcmovilcare' -keyalg RSA -keysize 2048 -validity 10000
$env:RELEASE_KEYSTORE = (Resolve-Path 'app\signing\mcmovilcare-release.jks').Path
$env:RELEASE_KEY_ALIAS = 'mcmovilcare'
$env:RELEASE_STORE_PASSWORD = 'TU_CLAVE_DEL_ALMACEN'
$env:RELEASE_KEY_PASSWORD = 'TU_CLAVE_DE_LA_LLAVE'
.\gradlew.bat ':app:assembleRelease'
```

El artefacto se genera en `app/build/outputs/apk/release/app-release.apk`. Conserva de forma segura una copia de respaldo de la llave: no es posible volver a firmar actualizaciones con una llave perdida. La configuración Gradle lee las variables anteriores y no contiene secretos. Para cambiar el endpoint del backend al crear el release, agrega `-PapiBaseUrl=https://<servidor>/`.

## Entrega colaborativa

El código fuente se publica en [GitHub](https://github.com/KrosTrag/AppMCMovilCare_GrupoRomero). La entrega también requiere commits de cada integrante y evidencia verificable de planificación en Trello; completa los nombres oficiales del equipo en la sección Integrantes y adjunta capturas o enlaces reales. No se inventan integrantes ni evidencias.

## Documentación técnica

Consulta [docs/TECNICA.md](docs/TECNICA.md) para detalles del diseño, API, decisiones de seguridad y proceso de publicación.