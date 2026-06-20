package com.club.atletismo.grupo;

import com.club.atletismo.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "grupo")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String disciplina;
    private String descripcion;

    @OneToMany(mappedBy = "grupo", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Usuario> atletas = new ArrayList<>();
}
