package com.example.tallerappmovil.model;

public class DatosFisicosRequest {
    private Long atletaId;
    private String fecha;
    private Double pesoKg;
    private Double alturaCm;
    private Double masaMuscularKg;
    private Double porcentajeGrasa;
    private String observaciones;

    public DatosFisicosRequest(Long atletaId, String fecha, Double pesoKg, Double alturaCm,
                                Double masaMuscularKg, Double porcentajeGrasa, String observaciones) {
        this.atletaId = atletaId;
        this.fecha = fecha;
        this.pesoKg = pesoKg;
        this.alturaCm = alturaCm;
        this.masaMuscularKg = masaMuscularKg;
        this.porcentajeGrasa = porcentajeGrasa;
        this.observaciones = observaciones;
    }
}
