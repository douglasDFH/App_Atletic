package com.example.tallerappmovil.model;

public class RegisterRequest {
    private String nombreCompleto;
    private String correo;
    private String contrasena;
    private String rol;

    public RegisterRequest(String nombreCompleto, String correo, String contrasena, String rol) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public String getCorreo()         { return correo; }
    public String getContrasena()     { return contrasena; }
    public String getRol()            { return rol; }
}
