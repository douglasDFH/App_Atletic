package com.club.atletismo.sesion;

import com.club.atletismo.grupo.Grupo;
import com.club.atletismo.grupo.GrupoRepository;
import com.club.atletismo.notificacion.NotificacionService;
import com.club.atletismo.sesion.dto.SesionRequest;
import com.club.atletismo.sesion.dto.SesionResponse;
import com.club.atletismo.usuario.Rol;
import com.club.atletismo.usuario.Usuario;
import com.club.atletismo.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class SesionService {

    private final SesionRepository sesionRepository;
    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Transactional(readOnly = true)
    public List<SesionResponse> listarPorSemana(String semana, Long grupoId) {
        LocalDateTime inicio = LocalDate.parse(semana, DATE).atStartOfDay();
        LocalDateTime fin = inicio.plusDays(7);
        List<SesionEntrenamiento> sesiones = grupoId != null
                ? sesionRepository.findBySemanaAndGrupo(inicio, fin, grupoId)
                : sesionRepository.findBySemana(inicio, fin);
        return sesiones.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public SesionResponse crear(SesionRequest req) {
        Grupo g = grupoRepository.findById(req.getGrupoId())
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));
        LocalDateTime horaInicio = LocalDateTime.parse(req.getHoraInicio(), DT);
        LocalDateTime horaFin    = LocalDateTime.parse(req.getHoraFin(), DT);
        log.info("crear() grupo={} horaInicio={} horaFin={} lugar={}",
                req.getGrupoId(), horaInicio, horaFin, req.getLugar());
        if (sesionRepository.countConflictos(req.getGrupoId(), horaInicio, horaFin, -1L) > 0) {
            throw new IllegalArgumentException(
                    "Ya existe una sesión programada para este grupo en ese horario");
        }
        SesionEntrenamiento s = SesionEntrenamiento.builder()
                .grupo(g)
                .horaInicio(horaInicio)
                .horaFin(horaFin)
                .lugar(req.getLugar())
                .descripcion(req.getDescripcion())
                .estado(EstadoSesion.PROGRAMADA)
                .build();
        // saveAndFlush fuerza el INSERT dentro de la transacción y confirma el id.
        SesionEntrenamiento guardada = sesionRepository.saveAndFlush(s);
        log.info("crear() sesión persistida id={} horaInicio={}",
                guardada.getId(), guardada.getHoraInicio());
        SesionResponse resp = toResponse(guardada);
        // Las notificaciones son secundarias: NUNCA deben revertir la sesión ya guardada.
        try {
            notificarGrupo(g.getId(), "SESION",
                    "Nueva sesión programada",
                    "Sesión el " + s.getHoraInicio().format(DATE) + " a las " +
                    s.getHoraInicio().format(DateTimeFormatter.ofPattern("HH:mm")) +
                    " en " + s.getLugar());
        } catch (Exception e) {
            log.error("crear() falló la notificación (sesión id={} SÍ guardada): {}",
                    guardada.getId(), e.toString(), e);
        }
        return resp;
    }

    @Transactional
    public SesionResponse editar(Long id, SesionRequest req) {
        SesionEntrenamiento s = sesionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));
        Grupo g = grupoRepository.findById(req.getGrupoId())
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));
        LocalDateTime horaInicio = LocalDateTime.parse(req.getHoraInicio(), DT);
        LocalDateTime horaFin    = LocalDateTime.parse(req.getHoraFin(), DT);
        if (sesionRepository.countConflictos(req.getGrupoId(), horaInicio, horaFin, id) > 0) {
            throw new IllegalArgumentException(
                    "Ya existe una sesión programada para este grupo en ese horario");
        }
        s.setGrupo(g);
        s.setHoraInicio(horaInicio);
        s.setHoraFin(horaFin);
        s.setLugar(req.getLugar());
        s.setDescripcion(req.getDescripcion());
        SesionResponse resp = toResponse(sesionRepository.save(s));
        notificarGrupo(g.getId(), "SESION",
                "Sesión modificada",
                "La sesión del " + s.getHoraInicio().format(DATE) + " fue actualizada — " + s.getLugar());
        return resp;
    }

    @Transactional
    public SesionResponse cancelar(Long id, Map<String, String> body) {
        SesionEntrenamiento s = sesionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));
        s.setEstado(EstadoSesion.CANCELADA);
        String motivo = body.get("motivo");
        s.setMotivoCancelacion(motivo);
        SesionResponse resp = toResponse(sesionRepository.save(s));
        notificarGrupo(s.getGrupo().getId(), "CANCELACION",
                "Sesión cancelada",
                "La sesión del " + s.getHoraInicio().format(DATE) + " fue cancelada" +
                (motivo != null && !motivo.isBlank() ? ": " + motivo : ""));
        return resp;
    }

    private void notificarGrupo(Long grupoId, String tipo, String titulo, String mensaje) {
        usuarioRepository.findByRolAndActivo(Rol.ATLETA, true).stream()
                .filter(u -> u.getGrupo() != null && u.getGrupo().getId().equals(grupoId))
                .forEach(u -> notificacionService.crear(u, tipo, titulo, mensaje));
    }

    @Transactional
    public void eliminar(Long id) {
        sesionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));
        sesionRepository.deleteById(id);
    }

    private SesionResponse toResponse(SesionEntrenamiento s) {
        return SesionResponse.builder()
                .id(s.getId())
                .grupoId(s.getGrupo().getId())
                .grupoNombre(s.getGrupo().getNombre())
                .lugar(s.getLugar())
                .descripcion(s.getDescripcion())
                .horaInicio(s.getHoraInicio().format(DT))
                .horaFin(s.getHoraFin().format(DT))
                .estado(s.getEstado().name())
                .motivoCancelacion(s.getMotivoCancelacion())
                .build();
    }
}
