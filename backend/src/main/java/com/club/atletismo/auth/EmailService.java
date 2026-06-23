package com.club.atletismo.auth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:noreply@atletismoscz.com}")
    private String fromAddress;

    @Value("${app.reset-url-scheme:atletismo://reset}")
    private String resetUrlScheme;

    public void sendVerificationEmail(String toEmail, String nombreCompleto, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("AthleteSCZ — Verifica tu correo electrónico");
            message.setText(
                    "Hola " + nombreCompleto + ",\n\n" +
                    "¡Bienvenido/a a AthleteSCZ! Para activar tu cuenta ingresa este código en la app:\n\n" +
                    token + "\n\n" +
                    "O toca este enlace desde tu teléfono:\n" +
                    "atletismo://verify?token=" + token + "\n\n" +
                    "Si no creaste esta cuenta, ignora este mensaje.\n\n" +
                    "— Club Atlético Santa Cruz de la Sierra"
            );
            mailSender.send(message);
            log.info("Correo de verificación enviado a {}", toEmail);
        } catch (Exception e) {
            log.error("Error al enviar correo de verificación a {}: {}", toEmail, e.getMessage());
            // No relanzar: el usuario queda registrado; puede reenviar manualmente
        }
    }

    public void sendPasswordResetEmail(String toEmail, String nombreCompleto, String token) {
        try {
            String resetLink = resetUrlScheme + "?token=" + token;

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("AthleteSCZ — Recuperación de contraseña");
            message.setText(
                    "Hola " + nombreCompleto + ",\n\n" +
                    "Has solicitado restablecer tu contraseña en AthleteSCZ.\n\n" +
                    "Toca el siguiente enlace desde tu teléfono:\n" +
                    resetLink + "\n\n" +
                    "O ingresa este código manualmente en la app:\n" +
                    token + "\n\n" +
                    "Este enlace expira en 1 hora.\n\n" +
                    "Si no solicitaste esto, ignora este mensaje.\n\n" +
                    "— Club Atlético Santa Cruz de la Sierra"
            );

            mailSender.send(message);
            log.info("Correo de recuperación enviado a {}", toEmail);

        } catch (Exception e) {
            log.error("Error al enviar correo a {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo de recuperación");
        }
    }
}
