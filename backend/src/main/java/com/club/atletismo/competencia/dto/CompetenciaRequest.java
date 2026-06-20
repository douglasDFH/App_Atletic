package com.club.atletismo.competencia.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CompetenciaRequest {
    @NotBlank
    private String nombre;
    private String disciplina;
    @NotNull
    private String fechaEvento;  // "2026-07-15"
    private String lugar;
    private String categoria;
    private String descripcion;
}
