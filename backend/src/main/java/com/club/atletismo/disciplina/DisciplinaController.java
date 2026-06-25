package com.club.atletismo.disciplina;

import com.club.atletismo.disciplina.dto.DisciplinaRequest;
import com.club.atletismo.disciplina.dto.DisciplinaResponse;
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
@RequestMapping("/api/v1/disciplinas")
@RequiredArgsConstructor
public class DisciplinaController {

    private final DisciplinaService disciplinaService;

    /** Todos los roles pueden ver las disciplinas activas (para spinners en la app) */
    @GetMapping
    public ResponseEntity<List<DisciplinaResponse>> listar() {
        return ResponseEntity.ok(disciplinaService.listar());
    }

    /** Solo entrenador/admin ve todas (incluyendo inactivas) para gestión */
    @GetMapping("/todas")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<List<DisciplinaResponse>> listarTodas() {
        return ResponseEntity.ok(disciplinaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DisciplinaResponse> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(disciplinaService.obtener(id));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<DisciplinaResponse> crear(@Valid @RequestBody DisciplinaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(disciplinaService.crear(req));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<DisciplinaResponse> actualizar(@PathVariable Long id,
                                                         @Valid @RequestBody DisciplinaRequest req) {
        return ResponseEntity.ok(disciplinaService.actualizar(id, req));
    }

    @PutMapping("/{id}/estado")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(@PathVariable Long id,
                                                           @RequestBody Map<String, Boolean> body) {
        disciplinaService.cambiarEstado(id, Boolean.TRUE.equals(body.get("activa")));
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
