package com.club.atletismo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

/**
 * Inicializa Firebase Admin SDK de forma OPCIONAL.
 * <p>
 * Orden de búsqueda de la credencial:
 * 1. Variable de entorno {@code FIREBASE_CREDENTIALS} con el JSON completo (recomendado en Coolify).
 * 2. Archivo {@code serviceAccountKey.json} en el classpath (desarrollo local).
 * <p>
 * Si no se encuentra ninguna o falla la inicialización, se registra un WARN y la aplicación
 * arranca igual: el push FCM quedará deshabilitado pero el resto del backend funciona.
 * Esto evita que el contenedor falle el healthcheck y Coolify haga rollback.
 */
@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${FIREBASE_CREDENTIALS:}")
    private String firebaseCredentialsJson;

    @PostConstruct
    public void initialize() {
        if (!FirebaseApp.getApps().isEmpty()) {
            return;
        }
        try {
            InputStream credentialStream = resolveCredentialStream();
            if (credentialStream == null) {
                log.warn("Firebase no inicializado: sin credencial (env FIREBASE_CREDENTIALS ni " +
                        "serviceAccountKey.json en classpath). Las notificaciones push estarán deshabilitadas.");
                return;
            }
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(credentialStream))
                    .build();
            FirebaseApp.initializeApp(options);
            log.info("Firebase Admin SDK inicializado correctamente.");
        } catch (Exception e) {
            log.warn("No se pudo inicializar Firebase ({}). Push FCM deshabilitado; el backend continúa.",
                    e.getMessage());
        }
    }

    private InputStream resolveCredentialStream() {
        // 1. Env var con el JSON completo (Coolify secret)
        if (firebaseCredentialsJson != null && !firebaseCredentialsJson.isBlank()) {
            log.info("Cargando credencial Firebase desde la variable de entorno FIREBASE_CREDENTIALS.");
            return new ByteArrayInputStream(firebaseCredentialsJson.getBytes(StandardCharsets.UTF_8));
        }
        // 2. Archivo en el classpath (desarrollo local)
        try {
            ClassPathResource resource = new ClassPathResource("serviceAccountKey.json");
            if (resource.exists()) {
                log.info("Cargando credencial Firebase desde serviceAccountKey.json (classpath).");
                return resource.getInputStream();
            }
        } catch (Exception e) {
            log.warn("Error al leer serviceAccountKey.json del classpath: {}", e.getMessage());
        }
        return null;
    }
}
