package com.example.tallerappmovil.model;

import java.util.List;

public class GrupoEvolucionDto {
    private Long atletaId;
    private String atletaNombre;
    private List<MarcaPersonal> marcas;

    public Long getAtletaId() { return atletaId; }
    public String getAtletaNombre() { return atletaNombre; }
    public List<MarcaPersonal> getMarcas() { return marcas; }
}
