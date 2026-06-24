package com.club.atletismo.marca.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GrupoEvolucionResponse {
    private Long atletaId;
    private String atletaNombre;
    private List<MarcaResponse> marcas;
}
