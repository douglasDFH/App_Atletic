package com.club.atletismo.disciplina.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class DisciplinaRequest {

    @NotBlank
    private String nombre;

    private String descripcion;

    @NotBlank
    private String unidad;

    private boolean esTiempo = true;

    private Double pesoMinKg;
    private Double pesoMaxKg;
    private Double alturaMinCm;
    private Double imcMin;
    private Double imcMax;
    private Double masaMuscularMinKg;
    private Double porcentajeGrasaMax;
}
