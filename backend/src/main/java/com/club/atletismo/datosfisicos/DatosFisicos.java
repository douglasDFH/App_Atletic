package com.club.atletismo.datosfisicos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "datos_fisicos")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class DatosFisicos {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long atletaId;

    @Column(nullable = false)
    private Long registradoPorId;

    @Column(nullable = false)
    private LocalDate fecha;

    @Column(nullable = false)
    private Double pesoKg;

    @Column(nullable = false)
    private Double alturaCm;

    private Double masaMuscularKg;
    private Double porcentajeGrasa;

    /** IMC calculado al registrar: peso / (altura_m)² */
    @Column(nullable = false)
    private Double imc;

    private String observaciones;

    @Builder.Default
    @Column(nullable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();
}
