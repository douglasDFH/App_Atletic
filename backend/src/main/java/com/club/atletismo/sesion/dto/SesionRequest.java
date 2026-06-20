package com.club.atletismo.sesion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SesionRequest {
    @NotNull
    private Long grupoId;
    @NotBlank
    private String horaInicio;   // "2026-06-16T10:00:00"
    @NotBlank
    private String horaFin;
    @NotBlank
    private String lugar;
    private String descripcion;
}
