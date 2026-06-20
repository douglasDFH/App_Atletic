package com.example.tallerappmovil.model;

public class PerfilUsuario {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String rol;
    private String fechaRegistro;
    private Long grupoId;
    private String grupoNombre;

    public Long getId()               { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail()          { return email; }
    public String getRol()            { return rol; }
    public String getFechaRegistro()  { return fechaRegistro; }
    public Long getGrupoId()          { return grupoId; }
    public String getGrupoNombre()    { return grupoNombre; }
}
