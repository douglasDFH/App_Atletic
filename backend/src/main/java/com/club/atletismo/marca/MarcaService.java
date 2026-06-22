package com.club.atletismo.marca;

import com.club.atletismo.marca.dto.MarcaRequest;
import com.club.atletismo.marca.dto.MarcaResponse;
import com.club.atletismo.sesion.SesionEntrenamiento;
import com.club.atletismo.sesion.SesionRepository;
import com.club.atletismo.usuario.Usuario;
import com.club.atletismo.usuario.UsuarioRepository;
import com.club.atletismo.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MarcaService {

    private final MarcaRepository marcaRepository;
    private final UsuarioRepository usuarioRepository;
    private final SesionRepository sesionRepository;
    private final UsuarioService usuarioService;

    @Transactional(readOnly = true)
    public List<MarcaResponse> getMarcas(Long atletaId, String disciplina) {
        // Un atleta solo ve SUS propias marcas; un padre ve las de su hijo vinculado.
        // Se ignora cualquier atletaId/disciplina recibido para evitar fuga de marcas ajenas.
        Usuario actual = usuarioService.getUsuarioActual();
        String rol = actual.getRol().name();
        if ("ATLETA".equals(rol)) {
            atletaId = actual.getId();
        } else if ("PADRE".equals(rol)) {
            atletaId = actual.getAtletaVinculado() != null ? actual.getAtletaVinculado().getId() : -1L;
        }

        List<MarcaPersonal> lista;
        if (atletaId != null && disciplina != null) {
            lista = marcaRepository.findByAtletaIdAndDisciplinaOrderByFechaDesc(atletaId, disciplina);
        } else if (atletaId != null) {
            lista = marcaRepository.findByAtletaIdOrderByFechaDesc(atletaId);
        } else if (disciplina != null) {
            lista = marcaRepository.findByDisciplinaOrderByFechaDesc(disciplina);
        } else {
            lista = marcaRepository.findAllByOrderByFechaDesc();
        }
        return lista.stream().map(this::toResponse).collect(Collectors.toList());
    }

    /** Ranking / leaderboard: todas las marcas (por disciplina si se indica), visible a cualquier rol. */
    @Transactional(readOnly = true)
    public List<MarcaResponse> getRanking(String disciplina) {
        List<MarcaPersonal> lista = (disciplina != null)
                ? marcaRepository.findByDisciplinaOrderByFechaDesc(disciplina)
                : marcaRepository.findAllByOrderByFechaDesc();
        return lista.stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public MarcaResponse registrar(MarcaRequest req) {
        Usuario atleta = usuarioRepository.findById(req.getAtletaId())
                .orElseThrow(() -> new IllegalArgumentException("Atleta no encontrado"));

        SesionEntrenamiento sesion = null;
        if (req.getSesionId() != null) {
            sesion = sesionRepository.findById(req.getSesionId()).orElse(null);
        }

        // Calcular si es mejor marca
        List<MarcaPersonal> anteriores = marcaRepository
                .findByAtletaIdAndDisciplinaOrderByFechaDesc(atleta.getId(), req.getDisciplina());

        boolean esMejor = anteriores.isEmpty() || esMejorResultado(req.getResultado(),
                anteriores.get(0).getResultado(), req.getDisciplina());

        if (esMejor) {
            // Desmarcar anteriores PRs
            anteriores.stream()
                    .filter(MarcaPersonal::isEsMejorMarca)
                    .forEach(m -> { m.setEsMejorMarca(false); marcaRepository.save(m); });
        }

        MarcaPersonal marca = MarcaPersonal.builder()
                .atleta(atleta)
                .disciplina(req.getDisciplina())
                .resultado(req.getResultado())
                .unidad(req.getUnidad())
                .fecha(LocalDate.parse(req.getFecha()))
                .esMejorMarca(esMejor)
                .observaciones(req.getObservaciones())
                .sesion(sesion)
                .build();

        return toResponse(marcaRepository.save(marca));
    }

    @Transactional
    public void eliminar(Long id) {
        marcaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Marca no encontrada"));
        marcaRepository.deleteById(id);
    }

    private boolean esMejorResultado(String nuevo, String anterior, String disciplina) {
        try {
            double vNuevo = Double.parseDouble(nuevo.replaceAll("[^0-9.,]", "").replace(",", "."));
            double vAnterior = Double.parseDouble(anterior.replaceAll("[^0-9.,]", "").replace(",", "."));
            // Para disciplinas de tiempo (m / km), menor es mejor
            boolean tiempoBasado = disciplina != null &&
                    (disciplina.contains("m") || disciplina.contains("km"));
            return tiempoBasado ? vNuevo < vAnterior : vNuevo > vAnterior;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private MarcaResponse toResponse(MarcaPersonal m) {
        return MarcaResponse.builder()
                .id(m.getId())
                .atletaId(m.getAtleta().getId())
                .atletaNombre(m.getAtleta().getNombreCompleto())
                .disciplina(m.getDisciplina())
                .resultado(m.getResultado())
                .unidad(m.getUnidad())
                .fecha(m.getFecha().toString())
                .esMejorMarca(m.isEsMejorMarca())
                .observaciones(m.getObservaciones())
                .sesionId(m.getSesion() != null ? m.getSesion().getId() : null)
                .build();
    }
}
