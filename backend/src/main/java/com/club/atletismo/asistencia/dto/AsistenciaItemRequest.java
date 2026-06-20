package com.club.atletismo.asistencia.dto;

import lombok.Data;

@Data
public class AsistenciaItemRequest {
    private Long atletaId;
    private String estado;
}
