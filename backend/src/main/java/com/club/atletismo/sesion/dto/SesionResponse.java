package com.club.atletismo.sesion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SesionResponse {
    private Long id;
    private Long grupoId;
    private String grupoNombre;
    private String lugar;
    private String descripcion;
    private String horaInicio;
    private String horaFin;
    private String estado;
    private String motivoCancelacion;
}
