package com.example.tallerappmovil.model;

public class ResultadoCompetencia {
    private Long id;
    private Long atletaId;
    private String atletaNombre;
    private Integer posicion;
    private String marca;
    private String observaciones;
    private boolean esMarcaPersonal;

    public Long getId()              { return id; }
    public Long getAtletaId()        { return atletaId; }
    public String getAtletaNombre()  { return atletaNombre; }
    public Integer getPosicion()     { return posicion; }
    public String getMarca()         { return marca; }
    public String getObservaciones() { return observaciones; }
    public boolean isEsMarcaPersonal(){ return esMarcaPersonal; }
}
