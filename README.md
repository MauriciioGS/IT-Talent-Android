# IT-Talent

1. Objetivo de la App
2. Descripción
3. Justificación técinica
4. Credenciales de acceso
5. Dependencias 

## 1. Objetivo de la App

IT Talent proporciona una plataforma al alcance de la mano para reclutamiento de talento para vacantes en el área de Tecnologías de la Información. Acoplandose perfectamente a los procesos de reclutamiento para puestos de trabajo remoto/hibrido y facilitándolos.

En IT Talent los reclutadores pueden:

- Publicar vacantes de empleo.
- Administrar las etapas de los proceso de reclutamiento publicados (Publicación, revisión de candidatos, Entrevistas, Evaluaciones, Finalización), con el objetivo de tener un mejor acercamiento y feedback con los candidatos.
- Tener acceso directo a la información profesional y de contacto de los candidatos para encontrar los mejores talentos.

En IT Talent los profesionales de TI pueden:

- Exponer su perfil profesional (CV digital) a los reclutadores.
- Encontrar las mejores ofertas para el rol profesional que ejercen.
- Aplicar a ofertas de trabajo y ver su claramente su avance en el proceso de reclutamiento, así como tener contacto con la persona encargada del reclutamiento. Así mejorando su experiencia al buscar empleo en TI

## 2. Descripción

**Visuales IT Talent**
![ic_logo](./assets/logotransparent.png)
![main_logo](./assets/logo-horizontal.png)
IT Talent se compone visualmente de tonos Azul que, según la psicología del color, denota profesionalidad, hace ver serias las cosas, además emite sinceridad y calma. Con toques terciarios de amarillo-naranja para meter positividad, modernidad, luminosidad e innovación, buscando eso, crear un espacio amigable, cómodo, innovador, moderno, que cumpla con la mayoría de recomendaciones y estándares de UI/UX para reunir y conectar personas llegando a mejorar la experiencia de la empleabilidad en el área de las tecnologías de la información.

Hablando del logo elegido, para este se ideo un diseño simple el cuál se comprende totalmente del color primario para generar presencia y no perder elementos del mismo añadiendo más colores.

[Más información de la propuesta visual](https://github.com/MauriciioGS/IT-Talent/blob/master/propuesta-visual/Propuesta%20visual%20IT%20Talent.pdf)

### [UIKit y Mockups](https://www.figma.com/file/a0kU68Db5N5UUVQri6ZeDc/UI-KIT-PROYECTO-ITTalent?node-id=301%3A3534&t=XWvh4krkFW9qKh1n-1)

## 3. Justificación técnica

IT Talent ofrece múltiples listados de contenido y está orientado únicamente a smartphones buscando un acercamiento rápido e inmediato entre talentos y reclutadores. Dado que la app gira en torno a las listas, la única orientación permitida es la "portrait" para una mejor experiencia visual. Claro que, a futuro se tiene contemplado añadir la compatibilidad responsiva para tabletas y cualquier orientación.

La App tiene como mínimo soporte a la API 24: Android 7.0 Nougat, alcanzando al rededor del 94% de los dispositivos Android en el mundo. Sin embargo, se puede llegar a notar algunas deficiencias visuales en esta mínima versión, la mejor experiencia visual de IT Talent se obtiene a partir de la API 26: Android 8.0 y hasta la más reciente API 33 Tiramisu.

Se han buscado API's no tan antiguas por la utilización de algunas bibliotecas con métodos cruciales deprecados en versiones anteriores a la elegida. Viendo esto por el lado del público objetivo, se tiene que la comunidad "Tech" o "IT" son entusiastas tecnológicos que aspiran a tener dispositivos modernos, es por ello que IT Talent apunta a versiones más recientes.

## 4. Credenciales de Acceso 

## 5. Dependencias

- Android Jetpack
    -  Android-KTX: extensiones de Kotlin para utilizar funciones de extensión, lambdas, parametros nombrados, valores predeterminados en parámetros y corrutinas. Dentro de Android KTX se incluyeron también las bibliotecas de extensión para el lenguaje Kotlin tales como:
        - Core KTX
        - Fragment KTX
        - Activity KTX
        - Lifecycle KTX: para administrar corrutinas según el ciclo de vida de Activities, framgents y ViewModels
        - ViewModel KTX: lanzamiento de corrutinas desde ViewModels
        - LiveData KTX: de la mano de ViewModel para suscribirse a cambios en tiempo deal de un objeto.
        - Navigation KTX: Componente de navegación para manejar destinos, aplicar arquitectura de una sola Activity y navegar entre fragmentos
        - Room KTX: para agregar compatibilidad de Room con las corrutinas
        - Firebase KTX: extensiones de Firebase para Kotlin
    - Room Database: para guardar datos locales del usuario de manera sencilla
    - Componente de Navegación de Android: Nav Component: ya se menciono este, pero se añaden las dependencias requeridas para utilizar el paso de argumentos seguro *Safe Args* entre destinos.
- Corrutinas de Kotlin: para ejecución de subprocesos (procesos en segundo plano) y manejar de mejor manera las peticiones a serivdor y bases de datos.
- Dagger Hilt: para inyección de dependencias en Android para trabajos repetitivos de creación de instancias de clases en clases que dependen de esas instancias.
- Firebase Analytics: para que se envíen datos sobre el uso de la app en los dispositivos que la instalan y ver un análisis de dichos datos en la consola de Google Firebase.
- Firebase Firestore: como solución al Backend se implemento esta base de datos No-SQL que proporciona Google como servicio para crear colecciones y documentos, actualizarlos, consultarlos y eliminarlos.
- Firebase Auth: para agregar autenticación a la aplicación de manera sencilla utilizando el servicio de Google Firebase, únicamente se implementa la autenticación con correo y contraseña.
- Glide: framework realizado por Bump Technologies para cargar imágenes de manera sencilla, utiliza la decodificación de medios y uso de cache.
- CircleImageView: API realizada por Henning Dodenhof que proporciona la implementación de imágenes dentro de una forma circular basado en RoundedImageView.
- Joda Time: se trata de una biblioteca realizada por Joda.org que proporciona una implementación fácil y sencilla de las clases Date y Time de Java para manejo de fechas y tiempo.
- LottieAnimationView: biblioteca para android de Design Barn Inc. que permite implementar animaciones en la UI a partir de archivos JSON descargados desde el sitio oficial de LottieFiles.
