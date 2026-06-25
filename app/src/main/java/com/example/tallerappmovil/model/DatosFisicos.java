package com.example.tallerappmovil.model;

public class DatosFisicos {
    private Long id;
    private Long atletaId;
    private String atletaNombre;
    private Long registradoPorId;
    private String registradoPorNombre;
    private String fecha;
    private Double pesoKg;
    private Double alturaCm;
    private Double masaMuscularKg;
    private Double porcentajeGrasa;
    private Double imc;
    private String observaciones;
    private String creadoEn;

    public Long getId()                    { return id; }
    public Long getAtletaId()              { return atletaId; }
    public String getAtletaNombre()        { return atletaNombre; }
    public Long getRegistradoPorId()       { return registradoPorId; }
    public String getRegistradoPorNombre() { return registradoPorNombre; }
    public String getFecha()               { return fecha; }
    public Double getPesoKg()              { return pesoKg; }
    public Double getAlturaCm()            { return alturaCm; }
    public Double getMasaMuscularKg()      { return masaMuscularKg; }
    public Double getPorcentajeGrasa()     { return porcentajeGrasa; }
    public Double getImc()                 { return imc; }
    public String getObservaciones()       { return observaciones; }
    public String getCreadoEn()            { return creadoEn; }
}
