package com.example.tallerappmovil.model;

public class SesionEntrenamiento {
    private Long id;
    private Long grupoId;
    private String grupoNombre;
    private String lugar;
    private String descripcion;
    private String horaInicio;       // ISO: "2026-06-16T10:00:00"
    private String horaFin;
    private String estado;           // PROGRAMADA | ACTIVA | FINALIZADA | CANCELADA
    private String motivoCancelacion;

    public Long getId()                   { return id; }
    public Long getGrupoId()              { return grupoId; }
    public String getGrupoNombre()        { return grupoNombre; }
    public String getLugar()              { return lugar; }
    public String getDescripcion()        { return descripcion; }
    public String getHoraInicio()         { return horaInicio; }
    public String getHoraFin()            { return horaFin; }
    public String getEstado()             { return estado; }
    public String getMotivoCancelacion()  { return motivoCancelacion; }
}
