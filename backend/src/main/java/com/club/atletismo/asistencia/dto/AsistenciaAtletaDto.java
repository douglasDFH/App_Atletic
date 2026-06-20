package com.club.atletismo.asistencia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AsistenciaAtletaDto {
    private Long atletaId;
    private String nombreCompleto;
    private String estado;
}
