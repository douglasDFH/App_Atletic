package com.example.tallerappmovil.model;

public class LoginRequest {
    private String correo;
    private String contrasena;
    private boolean recordarme;

    public LoginRequest(String correo, String contrasena, boolean recordarme) {
        this.correo     = correo;
        this.contrasena = contrasena;
        this.recordarme = recordarme;
    }

    public String  getCorreo()     { return correo; }
    public String  getContrasena() { return contrasena; }
    public boolean isRecordarme()  { return recordarme; }
}
