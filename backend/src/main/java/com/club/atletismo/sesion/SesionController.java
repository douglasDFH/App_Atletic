package com.club.atletismo.sesion;

import com.club.atletismo.grupo.dto.GrupoResumenDto;
import com.club.atletismo.grupo.GrupoService;
import com.club.atletismo.sesion.dto.SesionRequest;
import com.club.atletismo.sesion.dto.SesionResponse;
import com.club.atletismo.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SesionController {

    private final SesionService sesionService;
    private final GrupoService grupoService;

    @GetMapping("/api/v1/sesiones")
    public ResponseEntity<List<SesionResponse>> listar(
            @RequestParam String semana,
            @RequestParam(required = false) Long grupoId) {
        return ResponseEntity.ok(sesionService.listarPorSemana(semana, grupoId));
    }

    @PostMapping("/api/v1/sesiones")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<SesionResponse> crear(@Valid @RequestBody SesionRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(sesionService.crear(req));
    }

    @PutMapping("/api/v1/sesiones/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<SesionResponse> editar(@PathVariable Long id,
                                                  @Valid @RequestBody SesionRequest req) {
        return ResponseEntity.ok(sesionService.editar(id, req));
    }

    @PutMapping("/api/v1/sesiones/{id}/cancelar")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<SesionResponse> cancelar(@PathVariable Long id,
                                                    @RequestBody Map<String, String> body) {
        return ResponseEntity.ok(sesionService.cancelar(id, body));
    }

    @DeleteMapping("/api/v1/sesiones/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        sesionService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // Endpoint de grupos también accesible desde AgendaActivity
    @GetMapping("/api/v1/grupos-agenda")
    public ResponseEntity<List<GrupoResumenDto>> listarGrupos() {
        return ResponseEntity.ok(grupoService.listar());
    }
}
