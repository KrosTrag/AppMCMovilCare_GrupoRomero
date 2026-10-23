# AppMCMovilCare_GrupoRomero

Aplicación móvil desarrollada en Kotlin para Android, enfocada en la gestión y registro de información de usuarios mediante una interfaz ágil y moderna.

# Arquitectura y Tecnologías

El proyecto fue desarrollado siguiendo las mejores prácticas de desarrollo Android:

Jetpack Compose: Declaración de la interfaz de usuario de forma moderna y reactiva.

Arquitectura MVVM: Separación clara entre la vista y la lógica de negocio para un código mantenible.

Navigation Compose: Gestión del flujo entre pantallas sin pérdidas de datos.

StateFlow & ViewModel: Preservación del estado de los datos durante los ciclos de vida de la aplicación.

Estructura del Código

La solución esta organizada por paquetes según sus responsabilidades:

model/

UsuarioUiState.kt: Define la estructura de datos que representa el estado actual de la interfaz.

viewmodel/

UsuarioViewModel.kt: Gestiona la lógica de negocio, procesamiento del formulario y comunicación con la vista.

navigation/

Screen.kt: Define las rutas navegables de la aplicación de forma segura.

ui/

screens/: Contiene las vistas principales (HomeScreenCompacta, RegistroScreen, ResumenScreen).

components/: Componentes gráficos reutilizables (ImagenInteligente.kt).
