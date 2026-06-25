package com.example.tallerappmovil.model;

public class DisciplinaRequest {
    private String nombre;
    private String descripcion;
    private String unidad;
    private boolean esTiempo;
    private Double pesoMinKg;
    private Double pesoMaxKg;
    private Double alturaMinCm;
    private Double imcMin;
    private Double imcMax;
    private Double masaMuscularMinKg;
    private Double porcentajeGrasaMax;

    public DisciplinaRequest(String nombre, String descripcion, String unidad, boolean esTiempo,
                              Double pesoMinKg, Double pesoMaxKg, Double alturaMinCm,
                              Double imcMin, Double imcMax,
                              Double masaMuscularMinKg, Double porcentajeGrasaMax) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.unidad = unidad;
        this.esTiempo = esTiempo;
        this.pesoMinKg = pesoMinKg;
        this.pesoMaxKg = pesoMaxKg;
        this.alturaMinCm = alturaMinCm;
        this.imcMin = imcMin;
        this.imcMax = imcMax;
        this.masaMuscularMinKg = masaMuscularMinKg;
        this.porcentajeGrasaMax = porcentajeGrasaMax;
    }
}
