# Asistente de campañas (prototipo seguro) · Android universal

Proyecto Android adaptable a teléfonos y tabletas, en orientación vertical u horizontal, con WhatsApp Business instalado o como dispositivo vinculado. La compatibilidad declarada es Android 8.0 (API 26) a Android 16 (API 36), incluyendo la Redmi Pad SE 8.7 con HyperOS 3.

Incluye interfaz para campañas, imagen/texto, destinos y exclusiones, publicar ahora o programar, cola de 3–5 minutos, deduplicación persistente, estados, pausa/reanudación por red, notificaciones e historial base. El modo clientes exige confirmar consentimiento.

## Limitación deliberada

WhatsApp no ofrece a esta APK acceso oficial a los grupos existentes. El servicio de Accesibilidad está declarado, pero la versión 0.1 no pulsa automáticamente “Enviar”: los selectores visuales cambian y un falso positivo puede publicar en un chat incorrecto. Cada destino queda `REVIEW`. Para completar esa capa hay que capturar y validar la jerarquía de accesibilidad de la versión exacta de WhatsApp Business instalada, empezando con dos grupos propios.

No es una integración oficial de WhatsApp y no garantiza ausencia de restricciones.

## Android e iPhone no usan el mismo instalador

Una APK solo puede instalarse en Android. iPhone requiere una aplicación iOS independiente (`.ipa`) firmada con una cuenta de Apple Developer. iOS no permite que una aplicación controle la interfaz de WhatsApp ni pulse Enviar sin intervención. Por ello, una futura versión iOS solo puede administrar campañas, horarios, imágenes, clientes autorizados, avisos y abrir el flujo oficial de compartir para confirmación manual. El envío desatendido a chats o grupos existentes no está disponible en iOS.

La información de campañas puede mantenerse en un formato común para que futuras aplicaciones Android/iOS compartan los mismos campos, aunque la capacidad de envío sea distinta en cada sistema.

## Compilar en Android Studio

1. Instala Android Studio reciente y Android SDK 36.
2. Abre esta carpeta y deja que Gradle sincronice.
3. `Build > Build APK(s)`; el resultado estará en `app/build/outputs/apk/debug/app-debug.apk`.
4. Para distribución privada, usa `Build > Generate Signed App Bundle or APK` y conserva el keystore.

La interfaz no está fijada a orientación horizontal y se reajusta al tamaño disponible. En teléfonos pequeños se muestra dentro de un contenedor desplazable.

## Compilar automáticamente en GitHub

El proyecto incluye `.github/workflows/build-android.yml`. Al subirlo a la rama `main`, GitHub Actions instala Java, Gradle y Android SDK 36, ejecuta `assembleDebug` y publica `AsistenteCampanas-Android-debug` como artefacto durante 30 días. También puede iniciarse manualmente desde la pestaña Actions.

## Configurar HyperOS

Instala la APK, habilita notificaciones y Accesibilidad, permite inicio automático, configura batería “Sin restricciones”, bloquea la app y WhatsApp Business en Recientes y evita la limpieza automática. Mantén la tablet cargando y desbloqueada durante pruebas. Verifica que el dispositivo vinculado esté activo.

## Prueba mínima

Usa dos grupos propios, una imagen de prueba y un texto inequívoco. Simula pérdida de Wi‑Fi, pausa/reanuda, reinicia la app y confirma que ningún destino se duplica. No cargues clientes sin consentimiento.
