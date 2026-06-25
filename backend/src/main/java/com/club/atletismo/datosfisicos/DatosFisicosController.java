package com.club.atletismo.datosfisicos;

import com.club.atletismo.datosfisicos.dto.DatosFisicosRequest;
import com.club.atletismo.datosfisicos.dto.DatosFisicosResponse;
import com.club.atletismo.usuario.Rol;
import com.club.atletismo.usuario.Usuario;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/datos-fisicos")
@RequiredArgsConstructor
public class DatosFisicosController {

    private final DatosFisicosService service;

    /** Registrar medición física — solo entrenador/admin */
    @PostMapping
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<DatosFisicosResponse> registrar(
            @Valid @RequestBody DatosFisicosRequest req,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.registrar(req, usuario.getId()));
    }

    /** Historial completo de un atleta */
    @GetMapping("/{atletaId}")
    public ResponseEntity<List<DatosFisicosResponse>> historial(
            @PathVariable Long atletaId,
            @AuthenticationPrincipal Usuario usuario) {
        if (Rol.ATLETA.equals(usuario.getRol()) && !usuario.getId().equals(atletaId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(service.historial(atletaId));
    }

    /** Última medición de un atleta (para mostrar estado físico actual) */
    @GetMapping("/{atletaId}/ultimo")
    public ResponseEntity<DatosFisicosResponse> ultimo(
            @PathVariable Long atletaId,
            @AuthenticationPrincipal Usuario usuario) {
        if (Rol.ATLETA.equals(usuario.getRol()) && !usuario.getId().equals(atletaId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return service.ultimo(atletaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }
}
