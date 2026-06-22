package com.club.atletismo.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Cuenta PADRE/Tutor y el atleta (hijo) que tiene vinculado, si hay. */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PadreDto {
    private Long id;
    private String nombreCompleto;
    private String email;
    private Long hijoId;
    private String hijoNombre;
}
