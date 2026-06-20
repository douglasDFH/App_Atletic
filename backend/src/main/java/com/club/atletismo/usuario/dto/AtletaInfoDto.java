package com.club.atletismo.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class AtletaInfoDto {
    private Long id;
    private String nombreCompleto;
    private String disciplina;
}
