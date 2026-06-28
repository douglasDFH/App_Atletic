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
            String firstName = nombreCompleto != null && nombreCompleto.contains(" ")
                    ? nombreCompleto.split(" ")[0] : nombreCompleto;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("Tu codigo de acceso — Club Atletico Santa Cruz");
            message.setText(
                    "Hola " + firstName + ",\n\n" +
                    "Gracias por registrarte en AthleteSCZ.\n\n" +
                    "Tu codigo de verificacion es:\n\n" +
                    "    " + token + "\n\n" +
                    "Abre la app, pega el codigo en el campo de verificacion y toca \"Verificar cuenta\".\n\n" +
                    "Si no creaste esta cuenta, ignora este mensaje.\n\n" +
                    "Club Atletico Santa Cruz de la Sierra"
            );
            mailSender.send(message);
            log.info("Correo de verificacion enviado a {}", toEmail);
        } catch (Exception e) {
            log.error("Error al enviar correo de verificacion a {}: {}", toEmail, e.getMessage());
        }
    }

    public void sendPasswordResetEmail(String toEmail, String nombreCompleto, String token) {
        try {
            String firstName = nombreCompleto != null && nombreCompleto.contains(" ")
                    ? nombreCompleto.split(" ")[0] : nombreCompleto;
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromAddress);
            message.setTo(toEmail);
            message.setSubject("Restablecer contrasena — Club Atletico Santa Cruz");
            message.setText(
                    "Hola " + firstName + ",\n\n" +
                    "Recibimos una solicitud para restablecer la contrasena de tu cuenta en AthleteSCZ.\n\n" +
                    "Tu codigo de restablecimiento es:\n\n" +
                    "    " + token + "\n\n" +
                    "Abre la app, ingresa el codigo anterior y elige una nueva contrasena.\n" +
                    "Este codigo expira en 1 hora.\n\n" +
                    "Si no solicitaste esto, tu contrasena no ha cambiado.\n\n" +
                    "Club Atletico Santa Cruz de la Sierra"
            );
            mailSender.send(message);
            log.info("Correo de recuperacion enviado a {}", toEmail);
        } catch (Exception e) {
            log.error("Error al enviar correo de recuperacion a {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo de recuperacion");
        }
    }
}
