package com.club.atletismo.notificacion;

import com.club.atletismo.notificacion.dto.NotificacionDto;
import com.club.atletismo.shared.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notificaciones")
@RequiredArgsConstructor
public class NotificacionController {

    private final NotificacionService notificacionService;

    @GetMapping
    public ResponseEntity<List<NotificacionDto>> getMisNotificaciones() {
        return ResponseEntity.ok(notificacionService.getMisNotificaciones());
    }

    @PutMapping("/{id}/leer")
    public ResponseEntity<ApiResponse<Void>> marcarLeida(@PathVariable Long id) {
        notificacionService.marcarLeida(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PutMapping("/leer-todas")
    public ResponseEntity<ApiResponse<Void>> marcarTodasLeidas() {
        notificacionService.marcarTodasLeidas();
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
