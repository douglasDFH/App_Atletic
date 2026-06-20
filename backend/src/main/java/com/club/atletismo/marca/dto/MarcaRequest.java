package com.club.atletismo.marca.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MarcaRequest {
    @NotNull
    private Long atletaId;
    @NotBlank
    private String disciplina;
    @NotBlank
    private String resultado;
    private String unidad;
    @NotBlank
    private String fecha;  // "2026-06-18"
    private String observaciones;
    private Long sesionId;
}
