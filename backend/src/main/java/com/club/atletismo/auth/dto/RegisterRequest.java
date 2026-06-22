package com.club.atletismo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank(message = "El nombre es requerido")
    private String nombreCompleto;

    @NotBlank(message = "El correo es requerido")
    @Email(message = "Correo inválido")
    private String correo;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String contrasena;

    @Pattern(regexp = "ATLETA|PADRE", message = "Rol inválido")
    private String rol;

    // Datos del atleta (opcionales en el DTO; el servicio valida según rol/edad)
    private String fechaNacimiento;   // "2010-05-20"
    private String disciplina;

    // Datos del tutor — obligatorios si el atleta es menor de edad (se valida en AuthService)
    private String tutorNombre;
    private String tutorParentesco;
    private String tutorTelefono;
}
