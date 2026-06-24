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
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final GrupoRepository grupoRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        Grupo grupo = grupoRepository.findAll().stream()
                .filter(g -> "Velocidad Elite".equals(g.getNombre()))
                .findFirst()
                .orElseGet(() -> grupoRepository.save(Grupo.builder()
                        .nombre("Velocidad Elite")
                        .disciplina("100m / 200m")
                        .descripcion("Grupo principal de velocistas")
                        .build()));

        // Siempre garantiza el entrenador admin con credenciales correctas
        Usuario entrenador = usuarioRepository.findByCorreo("entrenador@atletismo.com")
                .orElseGet(() -> {
                    Usuario u = new Usuario();
                    u.setCorreo("entrenador@atletismo.com");
                    u.setNombreCompleto("Admin Entrenador");
                    u.setRol(Rol.ENTRENADOR);
                    u.setDisciplina("Velocidad");
                    return u;
                });

        entrenador.setContrasenaHash(passwordEncoder.encode("Admin123"));
        entrenador.setActivo(true);
        entrenador.setEmailVerificado(null); // null = cuenta legacy, sin restriccion de verificacion
        entrenador.setBloqueadoHasta(null);
        entrenador.setIntentosFallidos(0);
        usuarioRepository.save(entrenador);

        if (!usuarioRepository.existsByCorreo("atleta@atletismo.com")) {
            Usuario atleta1 = new Usuario();
            atleta1.setCorreo("atleta@atletismo.com");
            atleta1.setNombreCompleto("Carlos Mamani");
            atleta1.setContrasenaHash(passwordEncoder.encode("Atleta123"));
            atleta1.setRol(Rol.ATLETA);
            atleta1.setDisciplina("100m");
            atleta1.setCategoria("Senior");
            atleta1.setGrupo(grupo);
            atleta1.setActivo(true);
            atleta1.setEmailVerificado(null);
            usuarioRepository.save(atleta1);
        }

        if (!usuarioRepository.existsByCorreo("atleta2@atletismo.com")) {
            Usuario atleta2 = new Usuario();
            atleta2.setCorreo("atleta2@atletismo.com");
            atleta2.setNombreCompleto("María Flores");
            atleta2.setContrasenaHash(passwordEncoder.encode("Atleta123"));
            atleta2.setRol(Rol.ATLETA);
            atleta2.setDisciplina("200m");
            atleta2.setCategoria("Junior");
            atleta2.setGrupo(grupo);
            atleta2.setActivo(true);
            atleta2.setEmailVerificado(null);
            usuarioRepository.save(atleta2);
        }
    }
}
