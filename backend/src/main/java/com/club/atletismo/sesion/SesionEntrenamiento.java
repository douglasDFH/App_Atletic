package com.club.atletismo.sesion;

import com.club.atletismo.grupo.Grupo;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "sesion_entrenamiento")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SesionEntrenamiento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id", nullable = false)
    private Grupo grupo;

    @Column(nullable = false)
    private LocalDateTime horaInicio;

    @Column(nullable = false)
    private LocalDateTime horaFin;

    @Column(nullable = false)
    private String lugar;

    @Column(length = 500)
    private String descripcion;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoSesion estado = EstadoSesion.PROGRAMADA;

    private String motivoCancelacion;
}
