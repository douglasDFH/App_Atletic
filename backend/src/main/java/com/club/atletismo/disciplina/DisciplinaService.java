package com.club.atletismo.disciplina;

import com.club.atletismo.disciplina.dto.DisciplinaRequest;
import com.club.atletismo.disciplina.dto.DisciplinaResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DisciplinaService {

    private final DisciplinaRepository disciplinaRepository;

    @Transactional(readOnly = true)
    public List<DisciplinaResponse> listar() {
        return disciplinaRepository.findByActivaTrueOrderByNombreAsc()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<DisciplinaResponse> listarTodas() {
        return disciplinaRepository.findAll()
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public DisciplinaResponse obtener(Long id) {
        return toResponse(buscar(id));
    }

    @Transactional
    public DisciplinaResponse crear(DisciplinaRequest req) {
        if (disciplinaRepository.existsByNombreIgnoreCase(req.getNombre())) {
            throw new IllegalArgumentException("Ya existe una disciplina con ese nombre");
        }
        Disciplina d = fromRequest(new Disciplina(), req);
        return toResponse(disciplinaRepository.save(d));
    }

    @Transactional
    public DisciplinaResponse actualizar(Long id, DisciplinaRequest req) {
        Disciplina d = buscar(id);
        disciplinaRepository.findByNombreIgnoreCase(req.getNombre())
                .ifPresent(existe -> {
                    if (!existe.getId().equals(id))
                        throw new IllegalArgumentException("Ya existe una disciplina con ese nombre");
                });
        fromRequest(d, req);
        return toResponse(disciplinaRepository.save(d));
    }

    @Transactional
    public void cambiarEstado(Long id, boolean activa) {
        Disciplina d = buscar(id);
        d.setActiva(activa);
        disciplinaRepository.save(d);
    }

    private Disciplina buscar(Long id) {
        return disciplinaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Disciplina no encontrada"));
    }

    private Disciplina fromRequest(Disciplina d, DisciplinaRequest req) {
        d.setNombre(req.getNombre().trim());
        d.setDescripcion(req.getDescripcion());
        d.setUnidad(req.getUnidad());
        d.setEsTiempo(req.isEsTiempo());
        d.setPesoMinKg(req.getPesoMinKg());
        d.setPesoMaxKg(req.getPesoMaxKg());
        d.setAlturaMinCm(req.getAlturaMinCm());
        d.setImcMin(req.getImcMin());
        d.setImcMax(req.getImcMax());
        d.setMasaMuscularMinKg(req.getMasaMuscularMinKg());
        d.setPorcentajeGrasaMax(req.getPorcentajeGrasaMax());
        return d;
    }

    public DisciplinaResponse toResponse(Disciplina d) {
        return DisciplinaResponse.builder()
                .id(d.getId())
                .nombre(d.getNombre())
                .descripcion(d.getDescripcion())
                .unidad(d.getUnidad())
                .esTiempo(d.isEsTiempo())
                .pesoMinKg(d.getPesoMinKg())
                .pesoMaxKg(d.getPesoMaxKg())
                .alturaMinCm(d.getAlturaMinCm())
                .imcMin(d.getImcMin())
                .imcMax(d.getImcMax())
                .masaMuscularMinKg(d.getMasaMuscularMinKg())
                .porcentajeGrasaMax(d.getPorcentajeGrasaMax())
                .activa(d.isActiva())
                .build();
    }
}
