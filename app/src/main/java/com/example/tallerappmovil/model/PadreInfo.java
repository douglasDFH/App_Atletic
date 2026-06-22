package com.example.tallerappmovil.model;

/** Cuenta PADRE/Tutor con el hijo que tiene vinculado (si hay). */
public class PadreInfo {
    private Long id;
    private String nombreCompleto;
    private String email;
    private Long hijoId;
    private String hijoNombre;

    public Long getId()               { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail()          { return email; }
    public Long getHijoId()           { return hijoId; }
    public String getHijoNombre()     { return hijoNombre; }
}
