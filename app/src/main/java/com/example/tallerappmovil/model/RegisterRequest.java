package com.example.tallerappmovil.model;

public class RegisterRequest {
    private String nombreCompleto;
    private String correo;
    private String contrasena;
    private String rol;

    // Datos del atleta
    private String fechaNacimiento;   // "2010-05-20"
    private String disciplina;

    // Datos del tutor (si el atleta es menor)
    private String tutorNombre;
    private String tutorParentesco;
    private String tutorTelefono;

    public RegisterRequest(String nombreCompleto, String correo, String contrasena, String rol) {
        this.nombreCompleto = nombreCompleto;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
    }

    public void setFechaNacimiento(String fechaNacimiento) { this.fechaNacimiento = fechaNacimiento; }
    public void setDisciplina(String disciplina)           { this.disciplina = disciplina; }
    public void setTutorNombre(String tutorNombre)         { this.tutorNombre = tutorNombre; }
    public void setTutorParentesco(String tutorParentesco) { this.tutorParentesco = tutorParentesco; }
    public void setTutorTelefono(String tutorTelefono)     { this.tutorTelefono = tutorTelefono; }

    public String getNombreCompleto() { return nombreCompleto; }
    public String getCorreo()         { return correo; }
    public String getContrasena()     { return contrasena; }
    public String getRol()            { return rol; }
    public String getFechaNacimiento(){ return fechaNacimiento; }
    public String getDisciplina()     { return disciplina; }
    public String getTutorNombre()      { return tutorNombre; }
    public String getTutorParentesco()  { return tutorParentesco; }
    public String getTutorTelefono()    { return tutorTelefono; }
}
