package com.example.tallerappmovil.model;

public class AsistenciaHistorial {
    private Long sesionId;
    private String grupoNombre;
    private String horaInicio;  // ISO: "2026-06-18T10:00:00"
    private String lugar;
    private String estado;      // PRESENTE | AUSENTE | JUSTIFICADO

    public Long getSesionId()     { return sesionId; }
    public String getGrupoNombre(){ return grupoNombre; }
    public String getHoraInicio() { return horaInicio; }
    public String getLugar()      { return lugar; }
    public String getEstado()     { return estado; }
}
