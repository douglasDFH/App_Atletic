package com.club.atletismo.competencia.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class CompetenciaResponse {
    private Long id;
    private String nombre;
    private String disciplina;
    private String fechaEvento;
    private String lugar;
    private String categoria;
    private String descripcion;
    private String estado;
    private boolean estaInscrito;
    private int totalInscritos;
}
