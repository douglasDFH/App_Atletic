package com.example.tallerappmovil.model;

public class PerfilUsuario {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String rol;
    private String fechaRegistro;
    private Long grupoId;
    private String grupoNombre;
    private String fotoUrl;
    // Solo rol PADRE: hijo (atleta) vinculado que observa
    private Long atletaVinculadoId;
    private String atletaVinculadoNombre;

    public Long getId()               { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail()          { return email; }
    public String getRol()            { return rol; }
    public String getFechaRegistro()  { return fechaRegistro; }
    public Long getGrupoId()          { return grupoId; }
    public String getGrupoNombre()    { return grupoNombre; }
    public String getFotoUrl()        { return fotoUrl; }
    public Long getAtletaVinculadoId()      { return atletaVinculadoId; }
    public String getAtletaVinculadoNombre(){ return atletaVinculadoNombre; }
}
