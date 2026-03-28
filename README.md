# SecretScan 📷🔒

Una aplicación Android diseñada para escanear y extraer texto de documentos o imágenes físicas de manera **100% offline y privada**. Ninguna información sale de tu dispositivo.

## ✨ Características Principales

- **Privacidad Total**: No requiere conexión a internet. Todo el procesamiento de Reconocimiento Óptico de Caracteres (OCR) se realiza localmente en el propio dispositivo.
- **Fuentes de Imagen Flexibles**: Toma una foto nueva con la cámara directamente desde la app o elige una imagen existente de tu galería.
- **Recorte Inteligente**: Selecciona solo el bloque de texto que te interesa para una extracción más limpia.
- **Autodestrucción de Fotos**: Si tomas la foto con la cámara integrada, la imagen se elimina automáticamente una vez escaneada, no dejando rastros en el dispositivo. 
- **Exportación Anónima**: Guarda el texto extraído directamente en tu carpeta de Descargas como un archivo plano `.txt` con un nombre asignado al azar (ej: `xkqmr.txt`), o simplemente cópialo al portapapeles.

## 🛠️ Tecnologías Utilizadas

- **Kotlin** & **Android SDK** (API 35 optimizado, Edge-to-Edge).
- **Google ML Kit (On-Device Text Recognition)**: La magia del OCR ocurre usando el modelo en el propio teléfono (sin envío a la nube). Compatible con caracteres latinos (Español e Inglés).
- **uCrop**: Librería ágil y poderosa para el recorte de imágenes (con diseño Material oscuro personalizado).

## 📥 Descarga e Instalación

Puedes descargar el archivo APK listo para instalar desde la [sección de Releases](https://github.com/babinium/SecretScan/releases) de este repositorio.

1. Descarga el archivo `app-debug.apk`.
2. Transfiérelo a tu teléfono Android.
3. Permite la instalación desde *"Orígenes / Fuentes Desconocidas"*.
4. Instala y listo.

---

## 🏗️ Cómo compilarlo tú mismo (Modo Paranoico)

Si quieres mayor seguridad y auditar el código por ti mismo, puedes generar el APK desde tu propio ordenador. Solo necesitas tener Java 17 instalado.

1. Clona este repositorio:
   ```bash
   git clone https://github.com/babinium/SecretScan.git
   cd SecretScan
   ```

2. Ejecuta el Gradle Wrapper que viene incluido para compilar (él se encarga de descargar las dependencias):
   ```bash
   # En Linux o MacOS:
   ./gradlew assembleDebug

   # En Windows:
   gradlew.bat assembleDebug
   ```

3. El APK resultante aparecerá en la siguiente ruta:
   `app/build/outputs/apk/debug/app-debug.apk`

¡Disfruta escaneando tus documentos de forma segura! 🛡️
