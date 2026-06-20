package com.club.atletismo.usuario.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CambiarContrasenaRequest {
    @NotBlank
    private String contrasenaActual;
    @NotBlank @Size(min = 8)
    private String nuevaContrasena;
}
