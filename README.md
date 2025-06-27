**Nota:** Este proyecto fue desarrollado por el **Equipo 2 de Práctica de Desarrollo de Sistemas 3 (PDS3) 2025-1** en la **Universidad de Sonora**. Actualmente la app no está en funcionamiento porque el backend no está activo ni incluido en este repositorio. Si deseas acceso al backend o más información, puedes contactarme en **ebmv03@gmail.com**.

---

# 🌳 VerdeXpress

VerdeXpress es una plataforma diseñada para impulsar la conservación de áreas verdes en Hermosillo. A través de la app, las personas y empresas podrán realizar donaciones en especie o monetarias para apoyar el mantenimiento de parques y jardines, además de monitorear el progreso y las mejoras realizadas en cada parque, fomentando la transparencia y el compromiso con el medio ambiente.
## 🛠 Tecnologías

**Entorno de desarrollo:** Android Studio

**Lenguaje:** Kotlin


### ☁️ Servicios en la nube

**Autenticación:** Firebase Auth

**Base de datos:** Firebase Firestore

**Almacenamiento:** Supabase Storage

**Funciones en la nube:** Supabase Edge Functions


## 🖥️ Ejecutar la aplicación localmente

1. Abre **Android Studio**.

2. Selecciona **Clone Repository** en la pantalla de inicio.

3. En el campo **URL**, pega el enlace del proyecto (https://github.com/tokwyoo/VerdeXpress).

4. Android Studio descargará el proyecto automáticamente y lo abrirá en el IDE.

5. **Agrega el archivo `google-services.json` dentro de la carpeta `app`.**

   *Por motivos de seguridad, este archivo no se incluye en el repositorio y debe ser añadido manualmente.*  

   Si eres parte del equipo de **VerdeXpress**, solicita el archivo. Si no, puedes generar uno propio dentro de **Firebase** siguiendo estos pasos:  
   - Ve a la consola de Firebase.
   - Selecciona tu proyecto.
   - En la configuración del proyecto, descarga el archivo `google-services.json`.

6. **Sincroniza Gradle** (si es necesario).

7. **Conecta tu dispositivo o inicia un emulador.**

8. Haz clic en **Ejecutar** (el triángulo verde) para iniciar la aplicación.
## 🚀 Generar y desplegar la aplicación

1. Abre **Android Studio**.

2. Asegúrate de que el proyecto esté completamente sincronizado con **Gradle**.

3. En la barra superior de Android Studio, selecciona **Build** → **Build APK** (o **Build Bundle** si prefieres generar un App Bundle para Play Store).

4. Android Studio empezará a construir el APK o el bundle de la app. El proceso puede tardar algunos minutos.

5. Una vez finalizado, encontrarás el archivo APK en la siguiente ruta:
   - Para APK: `app/build/outputs/apk/debug/app-debug.apk` o `app/build/outputs/apk/release/app-release.apk` (dependiendo de la configuración que hayas elegido).
   - Para App Bundle: `app/build/outputs/bundle/release/app-release.aab`.

6. Si deseas instalar el APK en un dispositivo de prueba:
   - Conecta tu dispositivo Android.
   - Usa el comando `adb install path-to-apk` desde la terminal o simplemente instala el APK directamente en el dispositivo.

---

### Notas importantes:
- **Generar APK** es el proceso de crear el archivo instalable para Android.
- **Generar App Bundle (AAB)** es recomendable para distribución en la Google Play Store, ya que es más eficiente para la instalación.
