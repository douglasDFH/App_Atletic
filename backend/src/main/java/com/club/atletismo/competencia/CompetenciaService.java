package com.club.atletismo.competencia;

import com.club.atletismo.competencia.dto.CompetenciaRequest;
import com.club.atletismo.competencia.dto.CompetenciaResponse;
import com.club.atletismo.notificacion.NotificacionService;
import com.club.atletismo.usuario.Rol;
import com.club.atletismo.usuario.Usuario;
import com.club.atletismo.usuario.UsuarioRepository;
import com.club.atletismo.usuario.UsuarioService;
import com.club.atletismo.usuario.dto.AtletaInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetenciaService {

    private final CompetenciaRepository competenciaRepository;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;

    @Transactional(readOnly = true)
    public List<CompetenciaResponse> listar(String estado) {
        Usuario actual = usuarioService.getUsuarioActual();
        List<Competencia> lista = estado != null && !estado.isBlank()
                ? competenciaRepository.findByEstadoOrderByFechaEventoAsc(EstadoCompetencia.valueOf(estado))
                : competenciaRepository.findAllByOrderByFechaEventoAsc();
        return lista.stream().map(c -> toResponse(c, actual)).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CompetenciaResponse getDetalle(Long id) {
        Competencia c = competenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competencia no encontrada"));
        return toResponse(c, usuarioService.getUsuarioActual());
    }

    @Transactional
    public CompetenciaResponse crear(CompetenciaRequest req) {
        Competencia c = Competencia.builder()
                .nombre(req.getNombre())
                .disciplina(req.getDisciplina())
                .fechaEvento(LocalDate.parse(req.getFechaEvento()))
                .lugar(req.getLugar())
                .categoria(req.getCategoria())
                .descripcion(req.getDescripcion())
                .estado(EstadoCompetencia.PROXIMO)
                .build();
        CompetenciaResponse resp = toResponse(competenciaRepository.save(c), usuarioService.getUsuarioActual());
        usuarioRepository.findByRolAndActivo(Rol.ATLETA, true)
                .forEach(u -> notificacionService.crear(u, "COMPETENCIA",
                        "Nueva competencia: " + req.getNombre(),
                        req.getDisciplina() + " · " + req.getFechaEvento() + " en " + req.getLugar()));
        return resp;
    }

    @Transactional
    public CompetenciaResponse editar(Long id, CompetenciaRequest req) {
        Competencia c = competenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competencia no encontrada"));
        c.setNombre(req.getNombre());
        c.setDisciplina(req.getDisciplina());
        c.setFechaEvento(LocalDate.parse(req.getFechaEvento()));
        c.setLugar(req.getLugar());
        c.setCategoria(req.getCategoria());
        c.setDescripcion(req.getDescripcion());
        return toResponse(competenciaRepository.save(c), usuarioService.getUsuarioActual());
    }

    @Transactional
    public void eliminar(Long id) {
        competenciaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Competencia no encontrada"));
        competenciaRepository.deleteById(id);
    }

    @Transactional
    public void inscribirse(Long competenciaId) {
        Competencia c = competenciaRepository.findById(competenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Competencia no encontrada"));
        c.getInscritos().add(usuarioService.getUsuarioActual());
        competenciaRepository.save(c);
    }

    @Transactional
    public void desinscribirse(Long competenciaId) {
        Competencia c = competenciaRepository.findById(competenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Competencia no encontrada"));
        c.getInscritos().removeIf(u -> u.getId().equals(usuarioService.getUsuarioActual().getId()));
        competenciaRepository.save(c);
    }

    @Transactional(readOnly = true)
    public List<AtletaInfoDto> getInscritos(Long competenciaId) {
        Competencia c = competenciaRepository.findById(competenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Competencia no encontrada"));
        return c.getInscritos().stream()
                .map(u -> new AtletaInfoDto(u.getId(), u.getNombreCompleto(), u.getDisciplina()))
                .collect(Collectors.toList());
    }

    private CompetenciaResponse toResponse(Competencia c, Usuario actual) {
        boolean inscrito = c.getInscritos().stream()
                .anyMatch(u -> u.getId().equals(actual.getId()));
        return CompetenciaResponse.builder()
                .id(c.getId())
                .nombre(c.getNombre())
                .disciplina(c.getDisciplina())
                .fechaEvento(c.getFechaEvento().toString())
                .lugar(c.getLugar())
                .categoria(c.getCategoria())
                .descripcion(c.getDescripcion())
                .estado(c.getEstado().name())
                .estaInscrito(inscrito)
                .totalInscritos(c.getInscritos().size())
                .build();
    }
}
