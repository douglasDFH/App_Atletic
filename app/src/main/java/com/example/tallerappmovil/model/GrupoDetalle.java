package com.example.tallerappmovil.model;

import java.util.List;

public class GrupoDetalle {
    private Long id;
    private String nombre;
    private String disciplina;
    private String descripcion;
    private List<AtletaInfo> atletas;

    public Long getId()                  { return id; }
    public String getNombre()            { return nombre; }
    public String getDisciplina()        { return disciplina; }
    public String getDescripcion()       { return descripcion; }
    public List<AtletaInfo> getAtletas() { return atletas; }
}
