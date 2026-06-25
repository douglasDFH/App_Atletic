package com.club.atletismo.datosfisicos.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DatosFisicosResponse {

    private Long id;
    private Long atletaId;
    private String atletaNombre;
    private Long registradoPorId;
    private String registradoPorNombre;
    private String fecha;
    private Double pesoKg;
    private Double alturaCm;
    private Double masaMuscularKg;
    private Double porcentajeGrasa;
    private Double imc;
    private String observaciones;
    private String creadoEn;
}
