package com.club.atletismo.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank(message = "El correo es requerido")
    @Email(message = "Correo inválido")
    private String correo;

    @NotBlank(message = "La contraseña es requerida")
    private String contrasena;
}
