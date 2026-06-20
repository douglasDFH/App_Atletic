package com.club.atletismo.notificacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificacionDto {
    private Long id;
    private String tipo;
    private String titulo;
    private String mensaje;
    private String fecha;
    private boolean leida;
}
