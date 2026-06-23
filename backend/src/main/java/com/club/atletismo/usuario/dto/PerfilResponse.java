package com.club.atletismo.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PerfilResponse {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String rol;
    private String fechaRegistro;
    private Long grupoId;
    private String grupoNombre;
    private String fotoUrl;
    private String telefono;

    // Solo para rol PADRE: atleta (hijo) vinculado que observa
    private Long atletaVinculadoId;
    private String atletaVinculadoNombre;
}
