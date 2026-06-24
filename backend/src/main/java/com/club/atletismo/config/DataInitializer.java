package com.club.atletismo.config;

import com.club.atletismo.grupo.Grupo;
import com.club.atletismo.grupo.GrupoRepository;
import com.club.atletismo.usuario.Rol;
import com.club.atletismo.usuario.Usuario;
import com.club.atletismo.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final GrupoRepository grupoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        Grupo grupo = grupoRepository.findAll().stream()
                .filter(g -> "Velocidad Elite".equals(g.getNombre()))
                .findFirst()
                .orElseGet(() -> grupoRepository.save(Grupo.builder()
                        .nombre("Velocidad Elite")
                        .disciplina("100m / 200m")
                        .descripcion("Grupo principal de velocistas")
                        .build()));

        // Siempre sincroniza el entrenador admin (crea o restablece contraseña)
        usuarioRepository.findByCorreo("entrenador@atletismo.com").ifPresentOrElse(
            u -> {
                u.setContrasenaHash(passwordEncoder.encode("admin123"));
                u.setActivo(true);
                u.setBloqueadoHasta(null);
                u.setIntentosFallidos(0);
                usuarioRepository.save(u);
            },
            () -> usuarioRepository.save(Usuario.builder()
                .nombreCompleto("Admin Entrenador")
                .correo("entrenador@atletismo.com")
                .contrasenaHash(passwordEncoder.encode("admin123"))
                .rol(Rol.ENTRENADOR)
                .disciplina("Velocidad")
                .activo(true)
                .build())
        );

        if (!usuarioRepository.existsByCorreo("atleta@atletismo.com")) {
            usuarioRepository.save(Usuario.builder()
                    .nombreCompleto("Carlos Mamani")
                    .correo("atleta@atletismo.com")
                    .contrasenaHash(passwordEncoder.encode("atleta123"))
                    .rol(Rol.ATLETA)
                    .disciplina("100m")
                    .categoria("Senior")
                    .grupo(grupo)
                    .activo(true)
                    .build());
        }

        if (!usuarioRepository.existsByCorreo("atleta2@atletismo.com")) {
            usuarioRepository.save(Usuario.builder()
                    .nombreCompleto("María Flores")
                    .correo("atleta2@atletismo.com")
                    .contrasenaHash(passwordEncoder.encode("atleta123"))
                    .rol(Rol.ATLETA)
                    .disciplina("200m")
                    .categoria("Junior")
                    .grupo(grupo)
                    .activo(true)
                    .build());
        }
    }
}
