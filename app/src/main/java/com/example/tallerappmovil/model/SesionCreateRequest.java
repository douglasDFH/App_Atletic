package com.example.tallerappmovil.model;

public class SesionCreateRequest {
    private Long grupoId;
    private String horaInicio;   // "2026-06-16T10:00:00"
    private String horaFin;
    private String lugar;
    private String descripcion;

    public SesionCreateRequest(Long grupoId, String horaInicio, String horaFin,
                               String lugar, String descripcion) {
        this.grupoId     = grupoId;
        this.horaInicio  = horaInicio;
        this.horaFin     = horaFin;
        this.lugar       = lugar;
        this.descripcion = descripcion;
    }

    public Long getGrupoId()      { return grupoId; }
    public String getHoraInicio() { return horaInicio; }
    public String getHoraFin()    { return horaFin; }
    public String getLugar()      { return lugar; }
    public String getDescripcion(){ return descripcion; }
}
