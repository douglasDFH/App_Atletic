package com.example.tallerappmovil.model;

public class AtletaDetalle {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String disciplina;
    private String categoria;
    private String grupoNombre;
    private String estado;  // ACTIVO | INACTIVO

    public Long getId()              { return id; }
    public String getNombreCompleto(){ return nombreCompleto; }
    public String getEmail()         { return email; }
    public String getDisciplina()    { return disciplina; }
    public String getCategoria()     { return categoria; }
    public String getGrupoNombre()   { return grupoNombre; }
    public String getEstado()        { return estado; }
}
