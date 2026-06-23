package com.example.tallerappmovil.model;

public class CompetenciaRequest {
    private String nombre;
    private String disciplina;
    private String fechaEvento;
    private String lugar;
    private String categoria;
    private String descripcion;
    private Long grupoId; // null = convocar a todos (HU-09)

    public CompetenciaRequest(String nombre, String disciplina, String fechaEvento,
                               String lugar, String categoria, String descripcion, Long grupoId) {
        this.nombre      = nombre;
        this.disciplina  = disciplina;
        this.fechaEvento = fechaEvento;
        this.lugar       = lugar;
        this.categoria   = categoria;
        this.descripcion = descripcion;
        this.grupoId     = grupoId;
    }
}
