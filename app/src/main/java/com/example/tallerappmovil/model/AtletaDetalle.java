package com.example.tallerappmovil.model;

public class AtletaDetalle {
    private Long id;
    private String nombreCompleto;
    private String email;
    private String disciplina;
    private String categoria;
    private String grupoNombre;
    private String estado;  // ACTIVO | INACTIVO

    // Edad y datos del tutor (contacto de emergencia / menores)
    private String fechaNacimiento;
    private Integer edad;
    private boolean esMenor;
    private String tutorNombre;
    private String tutorParentesco;
    private String tutorTelefono;
    private Long tutorVinculadoId;
    private String tutorVinculadoNombre;

    public Long getId()              { return id; }
    public String getNombreCompleto(){ return nombreCompleto; }
    public String getEmail()         { return email; }
    public String getDisciplina()    { return disciplina; }
    public String getCategoria()     { return categoria; }
    public String getGrupoNombre()   { return grupoNombre; }
    public String getEstado()        { return estado; }
    public String getFechaNacimiento(){ return fechaNacimiento; }
    public Integer getEdad()         { return edad; }
    public boolean isEsMenor()       { return esMenor; }
    public String getTutorNombre()      { return tutorNombre; }
    public String getTutorParentesco()  { return tutorParentesco; }
    public String getTutorTelefono()    { return tutorTelefono; }
    public Long getTutorVinculadoId()     { return tutorVinculadoId; }
    public String getTutorVinculadoNombre(){ return tutorVinculadoNombre; }
}
