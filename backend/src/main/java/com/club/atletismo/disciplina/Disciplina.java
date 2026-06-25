package com.club.atletismo.disciplina;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "disciplina")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Disciplina {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;

    private String descripcion;

    /** Unidad de medida del resultado: "s" = segundos, "m" = metros, "pts" = puntos */
    @Column(nullable = false)
    private String unidad;

    /** true = menor valor es mejor (carreras), false = mayor valor es mejor (saltos/lanzamientos/gimnasia) */
    @Builder.Default
    @Column(nullable = false)
    private boolean esTiempo = true;

    // ── Condiciones físicas mínimas para participar (null = sin requisito) ──

    private Double pesoMinKg;
    private Double pesoMaxKg;
    private Double alturaMinCm;
    private Double imcMin;
    private Double imcMax;
    private Double masaMuscularMinKg;
    private Double porcentajeGrasaMax;

    @Builder.Default
    @Column(nullable = false)
    private boolean activa = true;
}
