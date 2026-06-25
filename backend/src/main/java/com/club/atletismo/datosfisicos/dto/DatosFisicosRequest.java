package com.club.atletismo.datosfisicos.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class DatosFisicosRequest {

    @NotNull
    private Long atletaId;

    /** Fecha ISO yyyy-MM-dd */
    @NotNull
    private String fecha;

    @NotNull
    private Double pesoKg;

    @NotNull
    private Double alturaCm;

    private Double masaMuscularKg;
    private Double porcentajeGrasa;
    private String observaciones;
}
