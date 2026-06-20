package com.club.atletismo.asistencia;

import com.club.atletismo.asistencia.dto.*;
import com.club.atletismo.shared.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class AsistenciaController {

    private final AsistenciaService asistenciaService;

    @GetMapping("/api/v1/sesiones/{id}/asistencia")
    public ResponseEntity<List<AsistenciaAtletaDto>> getAsistencia(@PathVariable Long id) {
        return ResponseEntity.ok(asistenciaService.getAsistenciaSesion(id));
    }

    @PostMapping("/api/v1/sesiones/{id}/asistencia")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> guardar(@PathVariable Long id,
                                                      @RequestBody List<AsistenciaItemRequest> lista) {
        asistenciaService.guardarAsistencia(id, lista);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @GetMapping("/api/v1/asistencia/mi-historial")
    public ResponseEntity<List<AsistenciaHistorialDto>> getMiHistorial() {
        return ResponseEntity.ok(asistenciaService.getMiHistorial());
    }

    @GetMapping("/api/v1/atletas/{id}/asistencia")
    public ResponseEntity<List<AsistenciaHistorialDto>> getHistorialAtleta(@PathVariable Long id) {
        return ResponseEntity.ok(asistenciaService.getHistorialAtleta(id));
    }

    @GetMapping("/api/v1/asistencia/reporte")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<List<ReporteAtletaDto>> getReporte(
            @RequestParam Long grupoId,
            @RequestParam String mes) {
        return ResponseEntity.ok(asistenciaService.getReporte(grupoId, mes));
    }
}
