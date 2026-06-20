package com.club.atletismo.marca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class MarcaResponse {
    private Long id;
    private Long atletaId;
    private String atletaNombre;
    private String disciplina;
    private String resultado;
    private String unidad;
    private String fecha;
    private boolean esMejorMarca;
    private String observaciones;
    private Long sesionId;
}
