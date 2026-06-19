package com.example.tallerappmovil.model;

public class EditarPerfilRequest {
    private String nombreCompleto;
    private String email;

    public EditarPerfilRequest(String nombreCompleto, String email) {
        this.nombreCompleto = nombreCompleto;
        this.email          = email;
    }

    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail()          { return email; }
}
