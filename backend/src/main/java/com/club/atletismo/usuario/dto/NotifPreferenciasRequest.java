package com.club.atletismo.usuario.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class NotifPreferenciasRequest {
    private Boolean sesiones;
    private Boolean competencias;
    private Boolean resultados;
}
