package com.example.tallerappmovil.model;

public class Competencia {
    private Long id;
    private String nombre;
    private String disciplina;
    private String fechaEvento;   // ISO: "2026-07-15"
    private String lugar;
    private String categoria;
    private String descripcion;
    private String estado;          // PROXIMO | EN_CURSO | FINALIZADO | CANCELADO
    private boolean estaInscrito;   // devuelto por backend para usuario autenticado
    private int totalInscritos;

    public Long getId()              { return id; }
    public String getNombre()        { return nombre; }
    public String getDisciplina()    { return disciplina; }
    public String getFechaEvento()   { return fechaEvento; }
    public String getLugar()         { return lugar; }
    public String getCategoria()     { return categoria; }
    public String getDescripcion()   { return descripcion; }
    public String getEstado()        { return estado; }
    public boolean isEstaInscrito()  { return estaInscrito; }
    public int getTotalInscritos()   { return totalInscritos; }
}
