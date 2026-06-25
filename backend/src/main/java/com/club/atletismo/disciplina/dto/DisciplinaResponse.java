package com.club.atletismo.disciplina.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class DisciplinaResponse {

    private Long id;
    private String nombre;
    private String descripcion;
    private String unidad;
    private boolean esTiempo;

    private Double pesoMinKg;
    private Double pesoMaxKg;
    private Double alturaMinCm;
    private Double imcMin;
    private Double imcMax;
    private Double masaMuscularMinKg;
    private Double porcentajeGrasaMax;

    private boolean activa;
}
