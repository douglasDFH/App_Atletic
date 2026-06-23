package com.club.atletismo.usuario.dto;

import lombok.Data;

/** Datos editables del perfil de un atleta por el entrenador (RF-04, HU-12). */
@Data
public class AtletaEditarRequest {
    private String nombreCompleto;
    private String disciplina;
    private String categoria;
    private String fechaNacimiento;   // "2010-05-20"
    private String tutorNombre;
    private String tutorParentesco;
    private String tutorTelefono;
}
