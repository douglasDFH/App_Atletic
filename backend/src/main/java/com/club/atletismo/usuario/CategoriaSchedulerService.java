package com.club.atletismo.usuario;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

/**
 * Actualiza automáticamente la categoría de cada atleta según su edad al cumplir años (HU-12).
 * Categorías del club: Pre-Infantil (≤10), Infantil (11-13), Juvenil (14-17), Mayores (≥18).
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CategoriaSchedulerService {

    private final UsuarioRepository usuarioRepository;

    @Scheduled(cron = "0 0 1 * * *")
    @Transactional
    public void actualizarCategorias() {
        List<Usuario> atletas = usuarioRepository.findByRolAndActivo(Rol.ATLETA, true);
        int actualizados = 0;
        for (Usuario u : atletas) {
            if (u.getFechaNacimiento() == null) continue;
            int edad = Period.between(u.getFechaNacimiento(), LocalDate.now()).getYears();
            String categoriaCalculada = categoriaPorEdad(edad);
            if (!categoriaCalculada.equals(u.getCategoria())) {
                u.setCategoria(categoriaCalculada);
                usuarioRepository.save(u);
                actualizados++;
                log.info("Categoría actualizada: {} ({} años) → {}",
                        u.getNombreCompleto(), edad, categoriaCalculada);
            }
        }
        if (actualizados > 0) {
            log.info("Scheduler categorías: {} atletas actualizados", actualizados);
        }
    }

    private String categoriaPorEdad(int edad) {
        if (edad <= 10) return "Pre-Infantil";
        if (edad <= 13) return "Infantil";
        if (edad <= 17) return "Juvenil";
        return "Mayores";
    }
}
