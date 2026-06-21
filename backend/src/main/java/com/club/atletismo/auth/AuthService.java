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

        Usuario usuario = Usuario.builder()
                .nombreCompleto(request.getNombreCompleto())
                .correo(request.getCorreo())
                .contrasenaHash(passwordEncoder.encode(request.getContrasena()))
                .rol(rol)
                .activo(true)
                .build();

        usuarioRepository.save(usuario);
    }

    public void forgotPassword(String correo) {
        passwordResetService.solicitarReset(correo);
    }

    public void resetPassword(String token, String nuevaContrasena) {
        passwordResetService.resetearPassword(token, nuevaContrasena);
    }
}
