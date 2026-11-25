# MCmovil Care

Aplicación móvil de servicio técnico para ingresar solicitudes de reparación, consultar y corregir el problema reportado, y gestionar las órdenes desde un perfil administrativo. El proyecto está alineado con el EFT como solución a la necesidad de trazabilidad y atención de equipos tecnológicos.

## Integrantes

Completar con los nombres oficiales del equipo antes de entregar.

## Funcionalidades

- Registro de solicitudes de diagnóstico desde Android con Kotlin y Jetpack Compose.
- Persistencia remota de órdenes en un microservicio Spring Boot con su propia base H2; Room mantiene una copia local de las solicitudes consultadas.
- Autenticación administrativa en un segundo microservicio Spring Boot con una base H2 independiente para usuarios y sesiones.
- Consulta y edición de la descripción del problema por el cliente, comprobando el ID de la orden y el correo asociado.
- Inicio de sesión exclusivo para el administrador. El panel permite listar solicitudes, avanzar su estado y eliminarlas.
- Consumo Retrofit de catálogo externo de teléfonos desde DummyJSON; si la API externa no responde, las demás funciones siguen disponibles.
- Pruebas unitarias en la validación del formulario Android y en servicios backend, con umbral JaCoCo del 80% para `TicketService` y `AdminAuthService`.
- Configuración Gradle para generar un APK release firmado usando variables de entorno y un almacén de claves local.

## Arquitectura

- `app/`: cliente Android Kotlin/Compose, Retrofit, Room y navegación.
- `backend/`: microservicio Spring Boot REST para solicitudes, con JPA y base H2 exclusiva de tickets.
- `auth-service/`: microservicio Spring Boot para login y sesiones de administrador, con JPA y base H2 exclusiva de autenticación.
- Los servicios no comparten tablas ni base de datos; el backend de tickets consulta al servicio de autenticación para autorizar el panel.
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

El microservicio de tickets usa H2 persistente en `backend/data/`; autenticación guarda usuarios y sesiones (contraseñas con BCrypt y sesiones con vencimiento) en `auth-service/data/`. El catálogo externo se consume desde `https://dummyjson.com/products/category/smartphones?limit=5`.

## Requisitos y ejecución local

Se requiere Android Studio/SDK Android y Java 17 o posterior. El servicio de tickets usa el puerto `8080` y el servicio de autenticación `8081`. Configura `ADMIN_EMAIL` y `ADMIN_PASSWORD` solo en el proceso de autenticación.

```powershell
$env:ADMIN_EMAIL = 'admin@tu-dominio.cl'
$env:ADMIN_PASSWORD = 'DefineUnaClaveLocalSegura'
.\gradlew.bat ':auth-service:bootRun'
```

En otro terminal ejecuta el servicio de tickets, apuntándolo al servicio de autenticación:

```powershell
$env:AUTH_SERVICE_URL = 'http://localhost:8081'
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
.\gradlew.bat ':auth-service:check'
```

`:backend:check` ejecuta las pruebas del servicio de tickets y falla si la cobertura de líneas de `TicketService` o su cliente de autenticación queda bajo 80%. `:auth-service:check` verifica el servicio de identidad y exige 80% de cobertura en `AdminAuthService`. Informes JaCoCo: `backend/build/reports/jacoco/test/html/index.html` y `auth-service/build/reports/jacoco/test/html/index.html`.

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