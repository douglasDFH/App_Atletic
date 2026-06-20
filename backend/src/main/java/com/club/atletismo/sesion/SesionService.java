package com.club.atletismo.sesion;

import com.club.atletismo.grupo.Grupo;
import com.club.atletismo.grupo.GrupoRepository;
import com.club.atletismo.sesion.dto.SesionRequest;
import com.club.atletismo.sesion.dto.SesionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SesionService {

    private final SesionRepository sesionRepository;
    private final GrupoRepository grupoRepository;

    private static final DateTimeFormatter DT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

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
        SesionEntrenamiento s = SesionEntrenamiento.builder()
                .grupo(g)
                .horaInicio(LocalDateTime.parse(req.getHoraInicio(), DT))
                .horaFin(LocalDateTime.parse(req.getHoraFin(), DT))
                .lugar(req.getLugar())
                .descripcion(req.getDescripcion())
                .estado(EstadoSesion.PROGRAMADA)
                .build();
        return toResponse(sesionRepository.save(s));
    }

    @Transactional
    public SesionResponse editar(Long id, SesionRequest req) {
        SesionEntrenamiento s = sesionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));
        Grupo g = grupoRepository.findById(req.getGrupoId())
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));
        s.setGrupo(g);
        s.setHoraInicio(LocalDateTime.parse(req.getHoraInicio(), DT));
        s.setHoraFin(LocalDateTime.parse(req.getHoraFin(), DT));
        s.setLugar(req.getLugar());
        s.setDescripcion(req.getDescripcion());
        return toResponse(sesionRepository.save(s));
    }

    @Transactional
    public SesionResponse cancelar(Long id, Map<String, String> body) {
        SesionEntrenamiento s = sesionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Sesión no encontrada"));
        s.setEstado(EstadoSesion.CANCELADA);
        s.setMotivoCancelacion(body.get("motivo"));
        return toResponse(sesionRepository.save(s));
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
