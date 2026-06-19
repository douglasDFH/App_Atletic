package com.example.tallerappmovil.model;

public class AtletaInfo {
    private Long id;
    private String nombreCompleto;
    private String disciplina;

    public Long getId()              { return id; }
    public String getNombreCompleto(){ return nombreCompleto; }
    public String getDisciplina()    { return disciplina; }

    @Override
    public String toString() { return nombreCompleto; }
}
