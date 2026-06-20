package com.club.atletismo.grupo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class GrupoRequest {
    @NotBlank
    private String nombre;
    private String disciplina;
    private String descripcion;
}
