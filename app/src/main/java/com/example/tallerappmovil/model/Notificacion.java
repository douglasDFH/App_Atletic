package com.example.tallerappmovil.model;

public class Notificacion {
    private Long id;
    private String tipo;     // SESION_NUEVA | ASISTENCIA_REGISTRADA | COMPETENCIA_NUEVA | NUEVA_MARCA | RECORDATORIO
    private String titulo;
    private String mensaje;
    private String fecha;    // ISO: "2026-06-18T10:00:00"
    private boolean leida;

    public Long getId()       { return id; }
    public String getTipo()   { return tipo; }
    public String getTitulo() { return titulo; }
    public String getMensaje(){ return mensaje; }
    public String getFecha()  { return fecha; }
    public boolean isLeida()  { return leida; }
    public void setLeida(boolean leida) { this.leida = leida; }
}
