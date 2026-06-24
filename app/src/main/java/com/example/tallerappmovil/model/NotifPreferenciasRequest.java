package com.example.tallerappmovil.model;

public class NotifPreferenciasRequest {
    private Boolean sesiones;
    private Boolean competencias;
    private Boolean resultados;

    public NotifPreferenciasRequest(Boolean sesiones, Boolean competencias, Boolean resultados) {
        this.sesiones     = sesiones;
        this.competencias = competencias;
        this.resultados   = resultados;
    }
}
