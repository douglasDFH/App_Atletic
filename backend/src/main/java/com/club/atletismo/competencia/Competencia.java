package com.club.atletismo.competencia;

import com.club.atletismo.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "competencia")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Competencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String disciplina;

    @Column(nullable = false)
    private LocalDate fechaEvento;

    private String lugar;
    private String categoria;

    @Column(length = 1000)
    private String descripcion;

    // null = notificar a TODOS los atletas; si tiene valor = solo ese grupo (HU-09)
    private Long grupoConvocadoId;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoCompetencia estado = EstadoCompetencia.PROXIMO;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "competencia_inscripcion",
               joinColumns = @JoinColumn(name = "competencia_id"),
               inverseJoinColumns = @JoinColumn(name = "usuario_id"))
    @Builder.Default
    private Set<Usuario> inscritos = new HashSet<>();
}
