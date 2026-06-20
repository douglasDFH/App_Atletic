package com.club.atletismo.grupo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class GrupoResumenDto {
    private Long id;
    private String nombre;
    private String disciplina;
}
