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
        if (usuarioRepository.count() > 0) return;

        Grupo grupo = grupoRepository.save(Grupo.builder()
                .nombre("Velocidad Elite")
                .disciplina("100m / 200m")
                .descripcion("Grupo principal de velocistas")
                .build());

        usuarioRepository.save(Usuario.builder()
                .nombreCompleto("Admin Entrenador")
                .correo("entrenador@atletismo.com")
                .contrasenaHash(passwordEncoder.encode("admin123"))
                .rol(Rol.ENTRENADOR)
                .disciplina("Velocidad")
                .activo(true)
                .build());

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
