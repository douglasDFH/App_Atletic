package com.example.tallerappmovil.model;

public class AtletaCrearRequest {
    private String nombreCompleto;
    private String correo;
    private String contrasena;
    private String disciplina;
    private String categoria;
    private String fechaNacimiento;
    private String tutorNombre;
    private String tutorParentesco;
    private String tutorTelefono;

    public AtletaCrearRequest(String nombreCompleto, String correo, String contrasena) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.contrasena = contrasena;
    }

    public void setDisciplina(String v)      { this.disciplina = v; }
    public void setCategoria(String v)       { this.categoria = v; }
    public void setFechaNacimiento(String v) { this.fechaNacimiento = v; }
    public void setTutorNombre(String v)     { this.tutorNombre = v; }
    public void setTutorParentesco(String v) { this.tutorParentesco = v; }
    public void setTutorTelefono(String v)   { this.tutorTelefono = v; }
}
