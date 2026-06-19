package com.club.atletismo.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String rol;
    private String nombreCompleto;
}
