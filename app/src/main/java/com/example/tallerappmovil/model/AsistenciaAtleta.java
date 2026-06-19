package com.example.tallerappmovil.model;

public class AsistenciaAtleta {
    private Long atletaId;
    private String nombreCompleto;
    private String estado; // PRESENTE | AUSENTE | JUSTIFICADO | null

    public Long getAtletaId()        { return atletaId; }
    public String getNombreCompleto(){ return nombreCompleto; }
    public String getEstado()        { return estado; }
}
