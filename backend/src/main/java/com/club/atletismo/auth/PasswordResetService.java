package com.club.atletismo.auth;

import com.club.atletismo.usuario.Usuario;
import com.club.atletismo.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordResetTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Transactional
    public void solicitarReset(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new IllegalArgumentException("Correo no encontrado"));

        // Eliminar tokens anteriores del usuario
        tokenRepository.deleteByUsuario(usuario);

        String token = UUID.randomUUID().toString();

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .usuario(usuario)
                .expiraEn(LocalDateTime.now().plusHours(1))
                .build();

        tokenRepository.save(resetToken);
        tokenRepository.flush();

        // Enviar email fuera de la transacción principal para no bloquearla
        emailService.sendPasswordResetEmail(usuario.getCorreo(), usuario.getNombreCompleto(), token);
    }

    @Transactional
    public void resetearPassword(String token, String nuevaContrasena) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido o no existe"));

        if (resetToken.isUsado()) {
            throw new IllegalArgumentException("Este enlace ya fue utilizado");
        }

        if (resetToken.getExpiraEn().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("El enlace ha expirado");
        }

        Usuario usuario = resetToken.getUsuario();
        usuario.setContrasenaHash(passwordEncoder.encode(nuevaContrasena));
        usuarioRepository.save(usuario);

        resetToken.setUsado(true);
        tokenRepository.save(resetToken);
    }
}
