package com.club.atletismo.marca;

import com.club.atletismo.marca.dto.MarcaRequest;
import com.club.atletismo.marca.dto.MarcaResponse;
import com.club.atletismo.shared.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/marcas")
@RequiredArgsConstructor
public class MarcaController {

    private final MarcaService marcaService;

    @GetMapping
    public ResponseEntity<List<MarcaResponse>> getMarcas(
            @RequestParam(required = false) Long atletaId,
            @RequestParam(required = false) String disciplina) {
        return ResponseEntity.ok(marcaService.getMarcas(atletaId, disciplina));
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<MarcaResponse> registrar(@Valid @RequestBody MarcaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(marcaService.registrar(req));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> eliminar(@PathVariable Long id) {
        marcaService.eliminar(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
