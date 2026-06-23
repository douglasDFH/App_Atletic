package com.club.atletismo.competencia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ResultadoResponse {
    private Long id;
    private Long atletaId;
    private String atletaNombre;
    private Integer posicion;
    private String marca;
    private String observaciones;
    private boolean esMarcaPersonal;
}
