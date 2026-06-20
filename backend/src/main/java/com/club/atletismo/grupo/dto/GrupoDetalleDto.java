package com.club.atletismo.grupo.dto;

import com.club.atletismo.usuario.dto.AtletaInfoDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class GrupoDetalleDto {
    private Long id;
    private String nombre;
    private String disciplina;
    private String descripcion;
    private List<AtletaInfoDto> atletas;
}
