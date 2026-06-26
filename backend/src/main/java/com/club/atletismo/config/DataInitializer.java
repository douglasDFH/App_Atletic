package com.club.atletismo.config;

import com.club.atletismo.disciplina.Disciplina;
import com.club.atletismo.disciplina.DisciplinaRepository;
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
    private final DisciplinaRepository disciplinaRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        seedDisciplinas();

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

    private void seedDisciplinas() {
        if (disciplinaRepository.count() > 0) return;

        Object[][] data = {
            // nombre,   descripcion,                              unidad, esTiempo, pesoMin, pesoMax, altMin, imcMin, imcMax, muscMin, grasaMax
            {"100m",    "Velocidad corta distancia",              "s",    true,  null,  null,  null,   null,  null,  null,  null  },
            {"200m",    "Velocidad media distancia",              "s",    true,  null,  null,  null,   null,  null,  null,  null  },
            {"400m",    "Velocidad larga distancia",              "s",    true,  null,  null,  null,   null,  27.0,  null,  22.0  },
            {"5k",      "Carrera de 5 kilómetros",               "s",    true,  null,  null,  null,   null,  28.0,  null,  25.0  },
            {"10k",     "Carrera de 10 kilómetros",              "s",    true,  null,  null,  null,   null,  27.0,  25.0,  22.0  },
            {"Salto Largo",         "Salto de longitud",         "m",    false, null,  null,  155.0,  null,  27.0,  null,  20.0  },
            {"Lanzamiento de Bala", "Lanzamiento de peso",       "m",    false, 55.0,  null,  null,   null,  null,  30.0,  null  },
            {"Gimnasia",            "Gimnasia artística",        "pts",  false, null,  65.0,  null,   null,  23.0,  null,  18.0  },
        };

        for (Object[] row : data) {
            Disciplina d = Disciplina.builder()
                    .nombre((String) row[0])
                    .descripcion((String) row[1])
                    .unidad((String) row[2])
                    .esTiempo((Boolean) row[3])
                    .pesoMinKg((Double) row[4])
                    .pesoMaxKg((Double) row[5])
                    .alturaMinCm((Double) row[6])
                    .imcMin((Double) row[7])
                    .imcMax((Double) row[8])
                    .masaMuscularMinKg((Double) row[9])
                    .porcentajeGrasaMax((Double) row[10])
                    .activa(true)
                    .build();
            disciplinaRepository.save(d);
        }
    }
}
