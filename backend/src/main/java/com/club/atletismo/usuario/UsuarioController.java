package com.club.atletismo.usuario;

import com.club.atletismo.shared.ApiResponse;
import com.club.atletismo.usuario.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @GetMapping("/api/v1/usuarios/perfil")
    public ResponseEntity<PerfilResponse> getPerfil() {
        return ResponseEntity.ok(usuarioService.getPerfil());
    }

    @PutMapping("/api/v1/usuarios/perfil")
    public ResponseEntity<PerfilResponse> editarPerfil(@Valid @RequestBody EditarPerfilRequest req) {
        return ResponseEntity.ok(usuarioService.editarPerfil(req));
    }

    @PutMapping("/api/v1/usuarios/cambiar-contrasena")
    public ResponseEntity<ApiResponse<Void>> cambiarContrasena(
            @Valid @RequestBody CambiarContrasenaRequest req) {
        usuarioService.cambiarContrasena(req);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PutMapping("/api/v1/usuarios/fcm-token")
    public ResponseEntity<ApiResponse<Void>> fcmToken(@RequestBody Map<String, String> body) {
        usuarioService.registrarFcmToken(body.get("token"));
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PutMapping("/api/v1/usuarios/foto-perfil")
    public ResponseEntity<PerfilResponse> subirFoto(@RequestPart("foto") MultipartFile foto)
            throws IOException {
        return ResponseEntity.ok(usuarioService.subirFoto(foto));
    }

    @GetMapping("/api/v1/atletas")
    public ResponseEntity<List<AtletaInfoDto>> getAtletas() {
        return ResponseEntity.ok(usuarioService.getAtletas());
    }

    @GetMapping("/api/v1/atletas/{id}")
    public ResponseEntity<AtletaDetalleDto> getAtleta(@PathVariable Long id) {
        return ResponseEntity.ok(usuarioService.getAtleta(id));
    }

    // ---- Gestión de atletas por el entrenador (RF-04, HU-12) ----

    @PostMapping("/api/v1/atletas")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> crearAtleta(@Valid @RequestBody AtletaCrearRequest req) {
        usuarioService.crearAtleta(req);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PutMapping("/api/v1/atletas/{id}")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<AtletaDetalleDto> editarAtleta(@PathVariable Long id,
                                                         @RequestBody AtletaEditarRequest req) {
        return ResponseEntity.ok(usuarioService.editarAtleta(id, req));
    }

    @PutMapping("/api/v1/atletas/{id}/estado")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> cambiarEstado(@PathVariable Long id,
                                                           @RequestBody Map<String, Boolean> body) {
        usuarioService.cambiarEstadoAtleta(id, Boolean.TRUE.equals(body.get("activo")));
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    // ---- Gestión de vínculo PADRE/Tutor ↔ atleta (solo entrenador/admin) ----

    @GetMapping("/api/v1/padres")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<List<PadreDto>> getPadres() {
        return ResponseEntity.ok(usuarioService.getPadres());
    }

    @PutMapping("/api/v1/padres/{padreId}/hijo/{atletaId}")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> vincularHijo(@PathVariable Long padreId,
                                                          @PathVariable Long atletaId) {
        usuarioService.vincularHijo(padreId, atletaId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @DeleteMapping("/api/v1/padres/{padreId}/hijo")
    @PreAuthorize("hasAnyRole('ENTRENADOR','ADMIN')")
    public ResponseEntity<ApiResponse<Void>> desvincularHijo(@PathVariable Long padreId) {
        usuarioService.desvincularHijo(padreId);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
