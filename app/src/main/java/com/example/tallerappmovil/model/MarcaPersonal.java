package com.example.tallerappmovil.model;

public class MarcaPersonal {
    private Long id;
    private Long atletaId;
    private String atletaNombre;
    private String disciplina;
    private String resultado;
    private String unidad;
    private String fecha;          // ISO: "2026-06-18"
    private boolean esMejorMarca;
    private String observaciones;
    private Long sesionId;

    public Long getId()              { return id; }
    public Long getAtletaId()        { return atletaId; }
    public String getAtletaNombre()  { return atletaNombre; }
    public String getDisciplina()    { return disciplina; }
    public String getResultado()     { return resultado; }
    public String getUnidad()        { return unidad; }
    public String getFecha()         { return fecha; }
    public boolean isEsMejorMarca()  { return esMejorMarca; }
    public String getObservaciones() { return observaciones; }
    public Long getSesionId()        { return sesionId; }
}
