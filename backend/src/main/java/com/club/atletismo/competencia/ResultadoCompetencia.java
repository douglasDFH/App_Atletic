package com.club.atletismo.competencia;

import com.club.atletismo.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "resultado_competencia",
       uniqueConstraints = @UniqueConstraint(columnNames = {"competencia_id", "atleta_id"}))
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ResultadoCompetencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competencia_id", nullable = false)
    private Competencia competencia;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id", nullable = false)
    private Usuario atleta;

    private Integer posicion;          // posición final
    private String marca;              // marca obtenida

    @Column(length = 500)
    private String observaciones;

    @Builder.Default
    private boolean esMarcaPersonal = false;
}
