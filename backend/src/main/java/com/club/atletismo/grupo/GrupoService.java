package com.club.atletismo.grupo;

import com.club.atletismo.grupo.dto.GrupoDetalleDto;
import com.club.atletismo.grupo.dto.GrupoRequest;
import com.club.atletismo.grupo.dto.GrupoResumenDto;
import com.club.atletismo.usuario.Rol;
import com.club.atletismo.usuario.Usuario;
import com.club.atletismo.usuario.UsuarioRepository;
import com.club.atletismo.usuario.dto.AtletaInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GrupoService {

    private final GrupoRepository grupoRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional(readOnly = true)
    public List<GrupoResumenDto> listar() {
        return grupoRepository.findAll().stream()
                .map(g -> new GrupoResumenDto(g.getId(), g.getNombre(), g.getDisciplina()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public GrupoDetalleDto getDetalle(Long id) {
        Grupo g = grupoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));

        List<AtletaInfoDto> atletas = usuarioRepository.findByRolAndActivo(Rol.ATLETA, true)
                .stream()
                .filter(u -> u.getGrupo() != null && u.getGrupo().getId().equals(id))
                .map(u -> new AtletaInfoDto(u.getId(), u.getNombreCompleto(), u.getDisciplina()))
                .collect(Collectors.toList());

        return GrupoDetalleDto.builder()
                .id(g.getId())
                .nombre(g.getNombre())
                .disciplina(g.getDisciplina())
                .descripcion(g.getDescripcion())
                .atletas(atletas)
                .build();
    }

    @Transactional
    public GrupoResumenDto crear(GrupoRequest req) {
        Grupo g = Grupo.builder()
                .nombre(req.getNombre())
                .disciplina(req.getDisciplina())
                .descripcion(req.getDescripcion())
                .build();
        g = grupoRepository.save(g);
        return new GrupoResumenDto(g.getId(), g.getNombre(), g.getDisciplina());
    }

    @Transactional
    public GrupoResumenDto editar(Long id, GrupoRequest req) {
        Grupo g = grupoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));
        g.setNombre(req.getNombre());
        g.setDisciplina(req.getDisciplina());
        g.setDescripcion(req.getDescripcion());
        g = grupoRepository.save(g);
        return new GrupoResumenDto(g.getId(), g.getNombre(), g.getDisciplina());
    }

    @Transactional
    public void eliminar(Long id) {
        Grupo g = grupoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));
        // Desasignar atletas antes de eliminar
        usuarioRepository.findByRolAndActivo(Rol.ATLETA, true).stream()
                .filter(u -> u.getGrupo() != null && u.getGrupo().getId().equals(id))
                .forEach(u -> { u.setGrupo(null); usuarioRepository.save(u); });
        grupoRepository.delete(g);
    }

    @Transactional
    public void agregarAtleta(Long grupoId, Long atletaId) {
        Grupo g = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new IllegalArgumentException("Grupo no encontrado"));
        Usuario u = usuarioRepository.findById(atletaId)
                .orElseThrow(() -> new IllegalArgumentException("Atleta no encontrado"));
        u.setGrupo(g);
        usuarioRepository.save(u);
    }

    @Transactional
    public void quitarAtleta(Long grupoId, Long atletaId) {
        Usuario u = usuarioRepository.findById(atletaId)
                .orElseThrow(() -> new IllegalArgumentException("Atleta no encontrado"));
        if (u.getGrupo() != null && u.getGrupo().getId().equals(grupoId)) {
            u.setGrupo(null);
            usuarioRepository.save(u);
        }
    }
}
