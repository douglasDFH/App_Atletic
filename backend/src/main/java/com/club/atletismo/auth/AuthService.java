package com.club.atletismo.auth;

import com.club.atletismo.auth.dto.LoginRequest;
import com.club.atletismo.auth.dto.RegisterRequest;
import com.club.atletismo.auth.dto.TokenResponse;
import com.club.atletismo.usuario.Rol;
import com.club.atletismo.usuario.Usuario;
import com.club.atletismo.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authManager;
    private final PasswordResetService passwordResetService;

    public TokenResponse login(LoginRequest request) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getCorreo(), request.getContrasena()));

        Usuario usuario = usuarioRepository.findByCorreo(request.getCorreo()).orElseThrow();
        String token = jwtService.generateToken(usuario);
        return new TokenResponse(token, token, usuario.getRol().name(), usuario.getNombreCompleto());
    }

    public void register(RegisterRequest request) {
        if (usuarioRepository.existsByCorreo(request.getCorreo())) {
            throw new IllegalArgumentException("El correo ya está registrado");
        }

        Rol rol = request.getRol() != null ? Rol.valueOf(request.getRol()) : Rol.ATLETA;

        Usuario.UsuarioBuilder builder = Usuario.builder()
                .nombreCompleto(request.getNombreCompleto())
                .correo(request.getCorreo())
                .contrasenaHash(passwordEncoder.encode(request.getContrasena()))
                .rol(rol)
                .activo(true);

        // Datos del atleta (fecha de nacimiento + validación de menor de edad)
        if (rol == Rol.ATLETA) {
            LocalDate fechaNac = parseFechaNacimiento(request.getFechaNacimiento());
            builder.fechaNacimiento(fechaNac)
                   .disciplina(request.getDisciplina());

            boolean esMenor = fechaNac != null
                    && Period.between(fechaNac, LocalDate.now()).getYears() < 18;
            if (esMenor) {
                // Protección de datos de menores: el tutor es obligatorio (RF-01, HU-01, RNF-02)
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

        usuarioRepository.save(builder.build());
    }

    private LocalDate parseFechaNacimiento(String fecha) {
        if (isBlank(fecha)) return null;
        try {
            return LocalDate.parse(fecha);
        } catch (Exception e) {
            throw new IllegalArgumentException("Fecha de nacimiento inválida (formato esperado: AAAA-MM-DD)");
        }
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
