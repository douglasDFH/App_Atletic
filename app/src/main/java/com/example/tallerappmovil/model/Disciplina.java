package com.example.tallerappmovil.model;

import com.google.gson.annotations.SerializedName;

public class Disciplina {
    private Long id;
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
    private boolean activa;

    public Long getId()                    { return id; }
    public String getNombre()             { return nombre; }
    public String getDescripcion()        { return descripcion; }
    public String getUnidad()             { return unidad; }
    public boolean isEsTiempo()           { return esTiempo; }
    public Double getPesoMinKg()          { return pesoMinKg; }
    public Double getPesoMaxKg()          { return pesoMaxKg; }
    public Double getAlturaMinCm()        { return alturaMinCm; }
    public Double getImcMin()             { return imcMin; }
    public Double getImcMax()             { return imcMax; }
    public Double getMasaMuscularMinKg()  { return masaMuscularMinKg; }
    public Double getPorcentajeGrasaMax() { return porcentajeGrasaMax; }
    public boolean isActiva()             { return activa; }

    /** Devuelve true si el atleta cumple los requisitos físicos de esta disciplina */
    public boolean atletaCumpleRequisitos(Double pesoKg, Double alturaCm,
                                          Double imc, Double masaMuscular,
                                          Double pctGrasa) {
        if (pesoMinKg    != null && pesoKg      != null && pesoKg      < pesoMinKg)    return false;
        if (pesoMaxKg    != null && pesoKg      != null && pesoKg      > pesoMaxKg)    return false;
        if (alturaMinCm  != null && alturaCm    != null && alturaCm    < alturaMinCm)  return false;
        if (imcMin       != null && imc         != null && imc         < imcMin)       return false;
        if (imcMax       != null && imc         != null && imc         > imcMax)       return false;
        if (masaMuscularMinKg != null && masaMuscular != null && masaMuscular < masaMuscularMinKg) return false;
        if (porcentajeGrasaMax != null && pctGrasa != null && pctGrasa > porcentajeGrasaMax)      return false;
        return true;
    }

    /** Texto descriptivo de los requisitos físicos de la disciplina */
    public String descripcionRequisitos() {
        StringBuilder sb = new StringBuilder();
        if (pesoMinKg    != null) sb.append("Peso mín: ").append(pesoMinKg).append(" kg  ");
        if (pesoMaxKg    != null) sb.append("Peso máx: ").append(pesoMaxKg).append(" kg  ");
        if (alturaMinCm  != null) sb.append("Altura mín: ").append(alturaMinCm).append(" cm  ");
        if (imcMin       != null) sb.append("IMC mín: ").append(imcMin).append("  ");
        if (imcMax       != null) sb.append("IMC máx: ").append(imcMax).append("  ");
        if (masaMuscularMinKg != null) sb.append("Masa musc. mín: ").append(masaMuscularMinKg).append(" kg  ");
        if (porcentajeGrasaMax != null) sb.append("Grasa máx: ").append(porcentajeGrasaMax).append("%");
        return sb.length() == 0 ? "Sin requisitos físicos mínimos" : sb.toString().trim();
    }
}
