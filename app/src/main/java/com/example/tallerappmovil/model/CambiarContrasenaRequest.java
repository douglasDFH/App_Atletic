package com.example.tallerappmovil.model;

public class CambiarContrasenaRequest {
    private String contrasenaActual;
    private String nuevaContrasena;

    public CambiarContrasenaRequest(String contrasenaActual, String nuevaContrasena) {
        this.contrasenaActual = contrasenaActual;
        this.nuevaContrasena  = nuevaContrasena;
    }

    public String getContrasenaActual() { return contrasenaActual; }
    public String getNuevaContrasena()  { return nuevaContrasena; }
}
