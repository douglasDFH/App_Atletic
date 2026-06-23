package com.club.atletismo.usuario.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class EditarPerfilRequest {
    @Email @NotBlank
    private String email;
    private String telefono;
    @NotBlank
    private String contrasenaActual;
}
