package com.club.atletismo.notificacion;

import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmService {

    // Asíncrono: el envío push NUNCA debe bloquear ni romper la operación que lo dispara
    // (crear/editar/cancelar sesión, crear competencia). Se ejecuta en un hilo aparte.
    @Async
    public void sendToToken(String token, String titulo, String mensaje) {
        if (token == null || token.isBlank()) return;
        // Si Firebase no se inicializó (sin credencial), no intentar enviar:
        // getInstance() lanzaría IllegalStateException y rompería el flujo que lo invoca.
        if (FirebaseApp.getApps().isEmpty()) return;
        try {
            Message message = Message.builder()
                    .setNotification(Notification.builder()
                            .setTitle(titulo)
                            .setBody(mensaje)
                            .build())
                    .setToken(token)
                    .build();
            FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            String prefijo = token.length() >= 10 ? token.substring(0, 10) : token;
            log.warn("FCM send failed for token {}: {}", prefijo, e.getMessage());
        }
    }
}
