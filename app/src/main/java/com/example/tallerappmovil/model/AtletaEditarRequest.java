package com.example.tallerappmovil.model;

public class AtletaEditarRequest {
    private String nombreCompleto;
    private String disciplina;
    private String categoria;
    private String fechaNacimiento;
    private String tutorNombre;
    private String tutorParentesco;
    private String tutorTelefono;

    public void setNombreCompleto(String v)  { this.nombreCompleto = v; }
    public void setDisciplina(String v)      { this.disciplina = v; }
    public void setCategoria(String v)       { this.categoria = v; }
    public void setFechaNacimiento(String v) { this.fechaNacimiento = v; }
    public void setTutorNombre(String v)     { this.tutorNombre = v; }
    public void setTutorParentesco(String v) { this.tutorParentesco = v; }
    public void setTutorTelefono(String v)   { this.tutorTelefono = v; }
}
