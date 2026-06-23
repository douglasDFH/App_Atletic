package com.club.atletismo.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/** Alta de un atleta por el entrenador (CU-02). Crea la cuenta con una contraseña inicial. */
@Data
public class AtletaCrearRequest {
    @NotBlank(message = "El nombre es requerido")
    private String nombreCompleto;

    @NotBlank(message = "El correo es requerido")
    @Email(message = "Correo inválido")
    private String correo;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;

    private String disciplina;
    private String categoria;
    private String fechaNacimiento;   // "2010-05-20"

    // Datos del tutor (obligatorios si es menor — se valida en el servicio)
    private String tutorNombre;
    private String tutorParentesco;
    private String tutorTelefono;
}
