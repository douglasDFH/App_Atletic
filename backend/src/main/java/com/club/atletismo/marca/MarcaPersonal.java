package com.club.atletismo.marca;

import com.club.atletismo.sesion.SesionEntrenamiento;
import com.club.atletismo.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "marca_personal")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MarcaPersonal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_id", nullable = false)
    private Usuario atleta;

    @Column(nullable = false)
    private String disciplina;

    @Column(nullable = false)
    private String resultado;

    private String unidad;

    @Column(nullable = false)
    private LocalDate fecha;

    @Builder.Default
    private boolean esMejorMarca = false;

    @Column(length = 500)
    private String observaciones;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sesion_id")
    private SesionEntrenamiento sesion;
}
