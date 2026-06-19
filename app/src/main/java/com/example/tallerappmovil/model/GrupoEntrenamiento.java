package com.example.tallerappmovil.model;

public class GrupoEntrenamiento {
    private Long id;
    private String nombre;
    private String disciplina;

    public Long getId()         { return id; }
    public String getNombre()   { return nombre; }
    public String getDisciplina() { return disciplina; }

    @Override
    public String toString() { return nombre; }
}
