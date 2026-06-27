package com.club.atletismo.competencia;

import com.club.atletismo.competencia.dto.CompetenciaRequest;
import com.club.atletismo.competencia.dto.CompetenciaResponse;
import com.club.atletismo.competencia.dto.ResultadoRequest;
import com.club.atletismo.competencia.dto.ResultadoResponse;
import com.club.atletismo.marca.MarcaService;
import com.club.atletismo.marca.dto.MarcaRequest;
import com.club.atletismo.marca.dto.MarcaResponse;
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
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompetenciaService {

    private final CompetenciaRepository competenciaRepository;
    private final UsuarioService usuarioService;
    private final UsuarioRepository usuarioRepository;
    private final NotificacionService notificacionService;
    private final ResultadoRepository resultadoRepository;
    private final MarcaService marcaService;

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
                .grupoConvocadoId(req.getGrupoId())
                .estado(EstadoCompetencia.PROXIMO)
                .build();
        CompetenciaResponse resp = toResponse(competenciaRepository.save(c), usuarioService.getUsuarioActual());
        // Notificación selectiva: si grupoId != null, solo ese grupo; si null, todos (HU-09)
        final Long grupoFiltro = req.getGrupoId();
        usuarioRepository.findByRolAndActivo(Rol.ATLETA, true).stream()
                .filter(u -> grupoFiltro == null
                        || (u.getGrupo() != null && u.getGrupo().getId().equals(grupoFiltro)))
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
        Usuario atleta = usuarioService.getUsuarioActual();
        c.getInscritos().add(atleta);
        competenciaRepository.save(c);

        String titulo = atleta.getNombreCompleto() + " se inscribió";
        String mensaje = atleta.getNombreCompleto() + " se inscribió en \"" + c.getNombre() + "\"";
        notificarEntrenadores(titulo, mensaje);
    }

    @Transactional
    public void desinscribirse(Long competenciaId) {
        Competencia c = competenciaRepository.findById(competenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Competencia no encontrada"));
        Usuario atleta = usuarioService.getUsuarioActual();
        c.getInscritos().removeIf(u -> u.getId().equals(atleta.getId()));
        competenciaRepository.save(c);

        String titulo = atleta.getNombreCompleto() + " canceló su inscripción";
        String mensaje = atleta.getNombreCompleto() + " canceló su inscripción en \"" + c.getNombre() + "\"";
        notificarEntrenadores(titulo, mensaje);
    }

    private void notificarEntrenadores(String titulo, String mensaje) {
        List<Usuario> destinatarios = new java.util.ArrayList<>(usuarioRepository.findByRol(Rol.ENTRENADOR));
        destinatarios.addAll(usuarioRepository.findByRol(Rol.ADMIN));
        destinatarios.forEach(e -> notificacionService.crear(e, "INSCRIPCION", titulo, mensaje));
    }

    @Transactional(readOnly = true)
    public List<AtletaInfoDto> getInscritos(Long competenciaId) {
        Competencia c = competenciaRepository.findById(competenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Competencia no encontrada"));
        return c.getInscritos().stream()
                .map(u -> new AtletaInfoDto(u.getId(), u.getNombreCompleto(), u.getDisciplina()))
                .collect(Collectors.toList());
    }

    // ---- Resultados de competencia (RF-15, HU-10) ----

    /** El entrenador registra el resultado de un atleta y lo asocia al historial de rendimiento. */
    @Transactional
    public ResultadoResponse registrarResultado(Long competenciaId, ResultadoRequest req) {
        Competencia c = competenciaRepository.findById(competenciaId)
                .orElseThrow(() -> new IllegalArgumentException("Competencia no encontrada"));
        Usuario atleta = usuarioRepository.findById(req.getAtletaId())
                .orElseThrow(() -> new IllegalArgumentException("Atleta no encontrado"));

        Optional<ResultadoCompetencia> existente =
                resultadoRepository.findByCompetenciaIdAndAtletaId(competenciaId, req.getAtletaId());
        boolean esNuevo = existente.isEmpty();
        ResultadoCompetencia r = existente.orElseGet(ResultadoCompetencia::new);
        r.setCompetencia(c);
        r.setAtleta(atleta);
        r.setPosicion(req.getPosicion());
        r.setMarca(req.getMarca());
        r.setObservaciones(req.getObservaciones());

        // Solo al registrar por primera vez: crear la marca en el historial de rendimiento
        // (asociación al rendimiento + detección automática de récord — HU-10)
        if (esNuevo && c.getDisciplina() != null && !c.getDisciplina().isBlank()
                && req.getMarca() != null && !req.getMarca().isBlank()) {
            MarcaRequest mr = new MarcaRequest();
            mr.setAtletaId(req.getAtletaId());
            mr.setDisciplina(c.getDisciplina());
            mr.setResultado(req.getMarca());
            mr.setFecha(c.getFechaEvento().toString());
            mr.setObservaciones("Competencia: " + c.getNombre());
            MarcaResponse marca = marcaService.registrar(mr);
            r.setEsMarcaPersonal(marca.isEsMejorMarca());
        }

        ResultadoCompetencia guardado = resultadoRepository.save(r);

        // Notificar al atleta sobre su resultado (HU-11)
        String mensajeResult = "Posición " + req.getPosicion() + " · Marca: " + req.getMarca()
                + (guardado.isEsMarcaPersonal() ? " ⭐ ¡Nuevo récord personal!" : "");
        notificacionService.crear(atleta, "RESULTADO",
                "Resultado registrado: " + c.getNombre(), mensajeResult);

        // Una competencia con resultados se considera finalizada
        if (c.getEstado() != EstadoCompetencia.FINALIZADO) {
            c.setEstado(EstadoCompetencia.FINALIZADO);
            competenciaRepository.save(c);
        }
        return toResultadoResponse(guardado);
    }

    @Transactional(readOnly = true)
    public List<ResultadoResponse> getResultados(Long competenciaId) {
        return resultadoRepository.findByCompetenciaIdOrderByPosicionAsc(competenciaId).stream()
                .map(this::toResultadoResponse)
                .collect(Collectors.toList());
    }

    private ResultadoResponse toResultadoResponse(ResultadoCompetencia r) {
        return ResultadoResponse.builder()
                .id(r.getId())
                .atletaId(r.getAtleta().getId())
                .atletaNombre(r.getAtleta().getNombreCompleto())
                .posicion(r.getPosicion())
                .marca(r.getMarca())
                .observaciones(r.getObservaciones())
                .esMarcaPersonal(r.isEsMarcaPersonal())
                .build();
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
                .grupoConvocadoId(c.getGrupoConvocadoId())
                .build();
    }
}
