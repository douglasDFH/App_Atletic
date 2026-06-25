package com.club.atletismo.datosfisicos;

import com.club.atletismo.datosfisicos.dto.DatosFisicosRequest;
import com.club.atletismo.datosfisicos.dto.DatosFisicosResponse;
import com.club.atletismo.usuario.Usuario;
import com.club.atletismo.usuario.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DatosFisicosService {

    private final DatosFisicosRepository repo;
    private final UsuarioRepository usuarioRepo;

    @Transactional
    public DatosFisicosResponse registrar(DatosFisicosRequest req, Long registradoPorId) {
        double alturaM = req.getAlturaCm() / 100.0;
        double imc = Math.round((req.getPesoKg() / (alturaM * alturaM)) * 100.0) / 100.0;

        DatosFisicos d = DatosFisicos.builder()
                .atletaId(req.getAtletaId())
                .registradoPorId(registradoPorId)
                .fecha(LocalDate.parse(req.getFecha()))
                .pesoKg(req.getPesoKg())
                .alturaCm(req.getAlturaCm())
                .masaMuscularKg(req.getMasaMuscularKg())
                .porcentajeGrasa(req.getPorcentajeGrasa())
                .imc(imc)
                .observaciones(req.getObservaciones())
                .creadoEn(LocalDateTime.now())
                .build();

        return toResponse(repo.save(d));
    }

    @Transactional(readOnly = true)
    public List<DatosFisicosResponse> historial(Long atletaId) {
        return repo.findByAtletaIdOrderByFechaDesc(atletaId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<DatosFisicosResponse> ultimo(Long atletaId) {
        return repo.findTopByAtletaIdOrderByFechaDesc(atletaId).map(this::toResponse);
    }

    private DatosFisicosResponse toResponse(DatosFisicos d) {
        String atletaNombre = usuarioRepo.findById(d.getAtletaId())
                .map(Usuario::getNombreCompleto).orElse("—");
        String registradoPorNombre = usuarioRepo.findById(d.getRegistradoPorId())
                .map(Usuario::getNombreCompleto).orElse("—");

        return DatosFisicosResponse.builder()
                .id(d.getId())
                .atletaId(d.getAtletaId())
                .atletaNombre(atletaNombre)
                .registradoPorId(d.getRegistradoPorId())
                .registradoPorNombre(registradoPorNombre)
                .fecha(d.getFecha().toString())
                .pesoKg(d.getPesoKg())
                .alturaCm(d.getAlturaCm())
                .masaMuscularKg(d.getMasaMuscularKg())
                .porcentajeGrasa(d.getPorcentajeGrasa())
                .imc(d.getImc())
                .observaciones(d.getObservaciones())
                .creadoEn(d.getCreadoEn().toString())
                .build();
    }
}
