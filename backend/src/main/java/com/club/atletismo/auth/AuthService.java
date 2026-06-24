package com.club.atletismo.auth;

import com.club.atletismo.auth.dto.LoginRequest;
import com.club.atletismo.auth.dto.RegisterRequest;
import com.club.atletismo.auth.dto.TokenResponse;
import com.club.atletismo.usuario.Rol;
import com.club.atletismo.usuario.Usuario;
import com.club.atletismo.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private static final int MAX_INTENTOS  = 5;
    private static final int BLOQUEO_MIN   = 15;

    private final UsuarioRepository    usuarioRepository;
    private final PasswordEncoder      passwordEncoder;
    private final JwtService           jwtService;
    private final PasswordResetService passwordResetService;
    private final EmailService         emailService;

    @Transactional
    public TokenResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new BadCredentialsException("Credenciales incorrectas"));

        // Cuenta bloqueada por demasiados intentos (HU-02)
        if (usuario.getBloqueadoHasta() != null
                && usuario.getBloqueadoHasta().isAfter(LocalDateTime.now())) {
            long minutos = ChronoUnit.MINUTES.between(LocalDateTime.now(), usuario.getBloqueadoHasta()) + 1;
            throw new CuentaBloqueadaException(
                    "Cuenta bloqueada. Intenta de nuevo en " + minutos + " minuto(s).");
        }

        // Correo no verificado — null = cuenta legacy anterior a HU-01 (no se bloquea)
        if (Boolean.FALSE.equals(usuario.getEmailVerificado())) {
            throw new CorreoNoVerificadoException(
                    "Verifica tu correo electrónico antes de iniciar sesión. Revisa tu bandeja de entrada.");
        }

        // Cuenta desactivada por entrenador (atleta dado de baja)
        if (!usuario.isActivo()) {
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getContrasena(), usuario.getContrasenaHash())) {
            int fallos = (usuario.getIntentosFallidos() != null ? usuario.getIntentosFallidos() : 0) + 1;
            usuario.setIntentosFallidos(fallos);
            if (fallos >= MAX_INTENTOS) {
                usuario.setBloqueadoHasta(LocalDateTime.now().plusMinutes(BLOQUEO_MIN));
                usuario.setIntentosFallidos(0);
            }
            usuarioRepository.save(usuario);
            throw new BadCredentialsException("Credenciales incorrectas");
        }

        // Login exitoso: limpiar contadores
        if ((usuario.getIntentosFallidos() != null && usuario.getIntentosFallidos() > 0)
                || usuario.getBloqueadoHasta() != null) {
            usuario.setIntentosFallidos(0);
            usuario.setBloqueadoHasta(null);
            usuarioRepository.save(usuario);
        }

        // Recordarme 30 días (HU-02)
        long expMs = request.isRecordarme()
                ? 30L * 24 * 60 * 60 * 1000
                : 86_400_000L;
        String token = jwtService.generateToken(usuario, expMs);
        return new TokenResponse(token, token, usuario.getRol().name(), usuario.getNombreCompleto());
    }

    @Transactional
    public void register(RegisterRequest request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }
        validarContrasena(request.getContrasena());

        Rol rol = request.getRol() != null ? Rol.valueOf(request.getRol()) : Rol.ATLETA;

        String tokenVerif = UUID.randomUUID().toString();

        Usuario.UsuarioBuilder builder = Usuario.builder()
                .nombreCompleto(request.getNombreCompleto())
                .correo(request.getCorreo())
                .contrasenaHash(passwordEncoder.encode(request.getContrasena()))
                .rol(rol)
                .activo(true)
                .emailVerificado(false)
                .tokenVerificacion(tokenVerif);

        // Datos del atleta (fecha de nacimiento + validación de menor de edad)
        if (rol == Rol.ATLETA) {
            LocalDate fechaNac = parseFechaNacimiento(request.getFechaNacimiento());
            builder.fechaNacimiento(fechaNac)
                   .disciplina(request.getDisciplina());

            boolean esMenor = fechaNac != null
                    && Period.between(fechaNac, LocalDate.now()).getYears() < 18;
            if (esMenor) {
                if (isBlank(request.getTutorNombre())
                        || isBlank(request.getTutorParentesco())
                        || isBlank(request.getTutorTelefono())) {
                    throw new IllegalArgumentException(
                            "El atleta es menor de edad: se requieren nombre, parentesco y teléfono del tutor");
                }
                builder.tutorNombre(request.getTutorNombre())
                       .tutorParentesco(request.getTutorParentesco())
                       .tutorTelefono(request.getTutorTelefono());
            }
        }

        Usuario usuario = usuarioRepository.save(builder.build());

        // Enviar correo de verificación (no bloquea si falla — el usuario queda registrado)
        emailService.sendVerificationEmail(usuario.getCorreo(), usuario.getNombreCompleto(), tokenVerif);
    }

    @Transactional
    public void verifyEmail(String token) {
        Usuario usuario = usuarioRepository.findByTokenVerificacion(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de verificación inválido o ya utilizado"));
        usuario.setEmailVerificado(true);
        usuario.setTokenVerificacion(null);
        usuarioRepository.save(usuario);
    }

    private LocalDate parseFechaNacimiento(String fecha) {
        if (isBlank(fecha)) return null;
        try {
            return LocalDate.parse(fecha);
        } catch (Exception e) {
            throw new IllegalArgumentException("Fecha de nacimiento inválida (formato esperado: AAAA-MM-DD)");
        }
    }

    private void validarContrasena(String pwd) {
        if (pwd == null || pwd.length() < 8)
            throw new IllegalArgumentException("La contraseña debe tener al menos 8 caracteres");
        if (!pwd.matches(".*[A-Z].*"))
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra mayúscula");
        if (!pwd.matches(".*[0-9].*"))
            throw new IllegalArgumentException("La contraseña debe contener al menos un número");
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }

    public void forgotPassword(String correo) {
        passwordResetService.solicitarReset(correo);
    }

    public void resetPassword(String token, String nuevaContrasena) {
        passwordResetService.resetearPassword(token, nuevaContrasena);
    }
}
