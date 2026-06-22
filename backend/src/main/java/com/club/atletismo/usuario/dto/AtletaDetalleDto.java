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

    // Datos de edad y tutor (contacto de emergencia / protección de datos de menores)
    private String fechaNacimiento;
    private Integer edad;
    private boolean esMenor;
    private String tutorNombre;
    private String tutorParentesco;
    private String tutorTelefono;

    // Cuenta PADRE vinculada a este atleta (si existe)
    private Long tutorVinculadoId;
    private String tutorVinculadoNombre;
}
