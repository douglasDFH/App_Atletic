package com.club.atletismo.grupo;

import com.club.atletismo.grupo.dto.GrupoDetalleDto;
import com.club.atletismo.grupo.dto.GrupoRequest;
import com.club.atletismo.grupo.dto.GrupoResumenDto;
import com.club.atletismo.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/grupos")
@RequiredArgsConstructor
public class GrupoController {

    private final GrupoService grupoService;

    @GetMapping
    public ResponseEntity<List<GrupoResumenDto>> listar() {
        return ResponseEntity.ok(grupoService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<GrupoDetalleDto> getDetalle(@PathVariable Long id) {
        return ResponseEntity.ok(grupoService.getDetalle(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<GrupoResumenDto> crear(@Valid @RequestBody GrupoRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(grupoService.crear(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<GrupoResumenDto> editar(@PathVariable Long id,
                                                   @Valid @RequestBody GrupoRequest req) {
        return ResponseEntity.ok(grupoService.editar(id, req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        grupoService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PostMapping("/{id}/atletas/{atletaId}")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> agregarAtleta(@PathVariable Long id,
                                                            @PathVariable Long atletaId) {
        grupoService.agregarAtleta(id, atletaId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/{id}/atletas/{atletaId}")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> quitarAtleta(@PathVariable Long id,
                                                           @PathVariable Long atletaId) {
        grupoService.quitarAtleta(id, atletaId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
