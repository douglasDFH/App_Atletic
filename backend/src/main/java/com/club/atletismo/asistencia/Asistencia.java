package com.club.atletismo.asistencia;

import com.club.atletismo.sesion.SesionEntrenamiento;
import com.club.atletismo.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "asistencia",
       uniqueConstraints = @UniqueConstraint(columnNames = {"sesion_id", "atleta_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Asistencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id", nullable = false)
    private SesionEntrenamiento sesion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id", nullable = false)
    private Usuario atleta;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoAsistencia estado;
}
