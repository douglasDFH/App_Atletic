package com.example.tallerappmovil.model;

public class AsistenciaRequest {
    private Long atletaId;
    private String estado;

    public AsistenciaRequest(Long atletaId, String estado) {
        this.atletaId = atletaId;
        this.estado   = estado;
    }

    public Long getAtletaId() { return atletaId; }
    public String getEstado() { return estado; }
}
