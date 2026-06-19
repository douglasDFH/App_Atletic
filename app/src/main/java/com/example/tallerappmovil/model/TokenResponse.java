package com.example.tallerappmovil.model;

public class TokenResponse {
    private String accessToken;
    private String refreshToken;
    private String rol;
    private String nombreCompleto;

    public String getAccessToken()    { return accessToken; }
    public String getRefreshToken()   { return refreshToken; }
    public String getRol()            { return rol; }
    public String getNombreCompleto() { return nombreCompleto; }
}
