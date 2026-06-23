package com.example.tallerappmovil.model;

public class ResultadoRequest {
    private Long atletaId;
    private Integer posicion;
    private String marca;
    private String observaciones;

    public ResultadoRequest(Long atletaId, Integer posicion, String marca, String observaciones) {
        this.atletaId = atletaId;
        this.posicion = posicion;
        this.marca = marca;
        this.observaciones = observaciones;
    }
}
