package com.example.tallerappmovil.model;

public class MarcaRequest {
    private Long atletaId;
    private String disciplina;
    private String resultado;
    private String unidad;
    private String fecha;
    private String observaciones;
    private Long sesionId;

    public MarcaRequest(Long atletaId, String disciplina, String resultado,
                        String unidad, String fecha, String observaciones) {
        this.atletaId    = atletaId;
        this.disciplina  = disciplina;
        this.resultado   = resultado;
        this.unidad      = unidad;
        this.fecha       = fecha;
        this.observaciones = observaciones;
    }

    public Long getAtletaId()        { return atletaId; }
    public String getDisciplina()    { return disciplina; }
    public String getResultado()     { return resultado; }
    public String getUnidad()        { return unidad; }
    public String getFecha()         { return fecha; }
    public String getObservaciones() { return observaciones; }
    public Long getSesionId()        { return sesionId; }
}
