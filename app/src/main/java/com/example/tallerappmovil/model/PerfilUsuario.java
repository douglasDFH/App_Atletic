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
    private String telefono;
    // Solo rol PADRE: hijo (atleta) vinculado que observa
    private Long atletaVinculadoId;
    private String atletaVinculadoNombre;

    // Preferencias de notificaciones push (HU-11)
    private Boolean notifSesiones;
    private Boolean notifCompetencias;
    private Boolean notifResultados;

    public Long getId()               { return id; }
    public String getNombreCompleto() { return nombreCompleto; }
    public String getEmail()          { return email; }
    public String getRol()            { return rol; }
    public String getFechaRegistro()  { return fechaRegistro; }
    public Long getGrupoId()          { return grupoId; }
    public String getGrupoNombre()    { return grupoNombre; }
    public String getFotoUrl()        { return fotoUrl; }
    public String getTelefono()       { return telefono; }
    public Long getAtletaVinculadoId()       { return atletaVinculadoId; }
    public String getAtletaVinculadoNombre() { return atletaVinculadoNombre; }
    public Boolean getNotifSesiones()        { return notifSesiones; }
    public Boolean getNotifCompetencias()    { return notifCompetencias; }
    public Boolean getNotifResultados()      { return notifResultados; }
}
