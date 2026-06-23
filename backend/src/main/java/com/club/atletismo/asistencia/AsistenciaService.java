package com.club.atletismo.asistencia;

import com.club.atletismo.asistencia.dto.*;
import com.club.atletismo.sesion.SesionEntrenamiento;
import com.club.atletismo.sesion.SesionRepository;
import com.club.atletismo.usuario.Rol;
import com.club.atletismo.usuario.Usuario;
import com.club.atletismo.usuario.UsuarioRepository;
import com.club.atletismo.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AsistenciaService {

    private final AsistenciaRepository asistenciaRepository;
    private final SesionRepository sesionRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @Transactional(readOnly = true)
    public List<AsistenciaAtletaDto> getAsistenciaSesion(Long sesionId) {
        SesionEntrenamiento sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));

        // Atletas del grupo
        List<Usuario> atletasGrupo = usuarioRepository.findByRolAndActivo(Rol.ATLETA, true)
                .stream()
                .filter(u -> u.getGrupo() != null && u.getGrupo().getId().equals(sesion.getGrupo().getId()))
                .collect(Collectors.toList());

        Map<Long, String> estadoMap = asistenciaRepository.findBySesionId(sesionId)
                .stream()
                .collect(Collectors.toMap(a -> a.getAtleta().getId(), a -> a.getEstado().name()));

        return atletasGrupo.stream()
                .map(u -> new AsistenciaAtletaDto(u.getId(), u.getNombreCompleto(),
                        estadoMap.getOrDefault(u.getId(), null)))
                .collect(Collectors.toList());
    }

    @Transactional
    public void guardarAsistencia(Long sesionId, List<AsistenciaItemRequest> lista) {
        SesionEntrenamiento sesion = sesionRepository.findById(sesionId)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));

        verificarPermisosAsistencia(sesionId, sesion.getHoraFin());

        for (AsistenciaItemRequest item : lista) {
            Usuario atleta = usuarioRepository.findById(item.getAtletaId())
                    .orElseThrow(() -> new IllegalArgumentException("Atleta no encontrado: " + item.getAtletaId()));

            Optional<Asistencia> existing = asistenciaRepository.findBySesionIdAndAtletaId(sesionId, item.getAtletaId());
            Asistencia a = existing.orElseGet(Asistencia::new);
            a.setSesion(sesion);
            a.setAtleta(atleta);
            a.setEstado(EstadoAsistencia.valueOf(item.getEstado()));
            asistenciaRepository.save(a);
        }
    }

    private void verificarPermisosAsistencia(Long sesionId, LocalDateTime horaFin) {
        if (horaFin.plusHours(2).isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "El plazo para registrar asistencia ha vencido (máximo 2 horas tras la sesión)");
        }
        boolean yaGuardada = !asistenciaRepository.findBySesionId(sesionId).isEmpty();
        if (yaGuardada) {
            boolean esAdmin = SecurityContextHolder.getContext().getAuthentication()
                    .getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            if (!esAdmin) {
                throw new IllegalArgumentException(
                        "La asistencia ya fue guardada. Solo el Administrador puede modificarla.");
            }
        }
    }

    @Transactional(readOnly = true)
    public List<AsistenciaHistorialDto> getMiHistorial() {
        Usuario u = usuarioService.getUsuarioActual();
        // Un padre ve el historial de asistencia de su hijo vinculado
        Long atletaId = u.getRol() == Rol.PADRE
                ? (u.getAtletaVinculado() != null ? u.getAtletaVinculado().getId() : -1L)
                : u.getId();
        return asistenciaRepository.findByAtletaIdOrderBySesionHoraInicioDesc(atletaId)
                .stream()
                .map(this::toHistorialDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AsistenciaHistorialDto> getHistorialAtleta(Long atletaId) {
        return asistenciaRepository.findByAtletaIdOrderBySesionHoraInicioDesc(atletaId)
                .stream()
                .map(this::toHistorialDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReporteAtletaDto> getReporte(Long grupoId, String mes) {
        YearMonth ym = YearMonth.parse(mes);
        LocalDateTime inicio = ym.atDay(1).atStartOfDay();
        LocalDateTime fin = ym.atEndOfMonth().atTime(23, 59, 59);

        List<Asistencia> asistencias = asistenciaRepository.findByGrupoAndPeriodo(grupoId, inicio, fin);

        Map<Long, List<Asistencia>> porAtleta = asistencias.stream()
                .collect(Collectors.groupingBy(a -> a.getAtleta().getId()));

        return porAtleta.entrySet().stream().map(entry -> {
            Long atletaId = entry.getKey();
            List<Asistencia> lista = entry.getValue();
            String nombre = lista.get(0).getAtleta().getNombreCompleto();
            int total = lista.size();
            int presentes = (int) lista.stream().filter(a -> a.getEstado() == EstadoAsistencia.PRESENTE).count();
            int ausentes = (int) lista.stream().filter(a -> a.getEstado() == EstadoAsistencia.AUSENTE).count();
            int justificados = (int) lista.stream().filter(a -> a.getEstado() == EstadoAsistencia.JUSTIFICADO).count();
            float pct = total > 0 ? (presentes * 100f / total) : 0f;
            return ReporteAtletaDto.builder()
                    .atletaId(atletaId)
                    .nombreCompleto(nombre)
                    .totalSesiones(total)
                    .presentes(presentes)
                    .ausentes(ausentes)
                    .justificados(justificados)
                    .porcentajeAsistencia(pct)
                    .build();
        }).sorted(Comparator.comparing(ReporteAtletaDto::getNombreCompleto)).collect(Collectors.toList());
    }

    private AsistenciaHistorialDto toHistorialDto(Asistencia a) {
        SesionEntrenamiento s = a.getSesion();
        return new AsistenciaHistorialDto(
                s.getId(),
                s.getGrupo().getNombre(),
                s.getHoraInicio().format(DT),
                s.getLugar(),
                a.getEstado().name()
        );
    }
}
