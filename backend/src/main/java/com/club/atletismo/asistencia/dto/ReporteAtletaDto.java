package com.club.atletismo.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ReporteAtletaDto {
    private Long atletaId;
    private String nombreCompleto;
    private int totalSesiones;
    private int presentes;
    private int ausentes;
    private int justificados;
    private float porcentajeAsistencia;
}
