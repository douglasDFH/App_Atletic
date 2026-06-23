package com.example.tallerappmovil.model;

public class EditarPerfilRequest {
    private String email;
    private String telefono;
    private String contrasenaActual;

    public EditarPerfilRequest(String email, String telefono, String contrasenaActual) {
        this.email           = email;
        this.telefono        = telefono;
        this.contrasenaActual = contrasenaActual;
    }

    public String getEmail()            { return email; }
    public String getTelefono()         { return telefono; }
    public String getContrasenaActual() { return contrasenaActual; }
}
