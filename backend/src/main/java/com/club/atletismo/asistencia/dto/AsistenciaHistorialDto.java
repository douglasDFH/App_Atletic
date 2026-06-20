package com.club.atletismo.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AsistenciaHistorialDto {
    private Long sesionId;
    private String grupoNombre;
    private String horaInicio;
    private String lugar;
    private String estado;
}
