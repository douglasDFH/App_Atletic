package com.club.atletismo.competencia.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ResultadoRequest {
    @NotNull(message = "El atleta es requerido")
    private Long atletaId;
    private Integer posicion;
    private String marca;
    private String observaciones;
}
