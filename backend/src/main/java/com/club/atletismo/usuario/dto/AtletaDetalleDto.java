package com.club.atletismo.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AtletaDetalleDto {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String disciplina;
    private String categoria;
    private String grupoNombre;
    private String estado;
}
