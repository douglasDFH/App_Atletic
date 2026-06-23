package com.club.atletismo.competencia;

import com.club.atletismo.competencia.dto.CompetenciaRequest;
import com.club.atletismo.competencia.dto.CompetenciaResponse;
import com.club.atletismo.competencia.dto.ResultadoRequest;
import com.club.atletismo.competencia.dto.ResultadoResponse;
import com.club.atletismo.shared.ApiResponse;
import com.club.atletismo.usuario.dto.AtletaInfoDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/competencias")
@RequiredArgsConstructor
public class CompetenciaController {

    private final CompetenciaService competenciaService;

    @GetMapping
    public ResponseEntity<List<CompetenciaResponse>> listar(
            @RequestParam(required = false) String estado) {
        return ResponseEntity.ok(competenciaService.listar(estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompetenciaResponse> getDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(competenciaService.getDetalle(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<CompetenciaResponse> crear(@Valid @RequestBody CompetenciaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(competenciaService.crear(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<CompetenciaResponse> editar(@PathVariable Long id,
                                                       @Valid @RequestBody CompetenciaRequest req) {
        return ResponseEntity.ok(competenciaService.editar(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        competenciaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/{id}/inscribir")
    public ResponseEntity<ApiResponse<Void>> inscribirse(@PathVariable Long id) {
        competenciaService.inscribirse(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/{id}/desinscribir")
    public ResponseEntity<ApiResponse<Void>> desinscribirse(@PathVariable Long id) {
        competenciaService.desinscribirse(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/{id}/inscritos")
    public ResponseEntity<List<AtletaInfoDto>> getInscritos(@PathVariable Long id) {
        return ResponseEntity.ok(competenciaService.getInscritos(id));
    }

    // ---- Resultados (RF-15, HU-10) ----

    @GetMapping("/{id}/resultados")
    public ResponseEntity<List<ResultadoResponse>> getResultados(@PathVariable Long id) {
        return ResponseEntity.ok(competenciaService.getResultados(id));
    }

    @PostMapping("/{id}/resultados")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<ResultadoResponse> registrarResultado(@PathVariable Long id,
                                                                @Valid @RequestBody ResultadoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(competenciaService.registrarResultado(id, req));
    }
}
