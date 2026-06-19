package com.example.tallerappmovil.model;

public class ReporteAtleta {
    private Long atletaId;
    private String nombreCompleto;
    private int totalSesiones;
    private int presentes;
    private int ausentes;
    private int justificados;
    private float porcentajeAsistencia;

    public Long getAtletaId()           { return atletaId; }
    public String getNombreCompleto()   { return nombreCompleto; }
    public int getTotalSesiones()       { return totalSesiones; }
    public int getPresentes()           { return presentes; }
    public int getAusentes()            { return ausentes; }
    public int getJustificados()        { return justificados; }
    public float getPorcentajeAsistencia() { return porcentajeAsistencia; }
}
