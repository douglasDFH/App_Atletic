package com.club.atletismo.notificacion;

import com.club.atletismo.usuario.Usuario;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacion")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false)
    private String tipo; // SESION_NUEVA | ASISTENCIA_REGISTRADA | COMPETENCIA_NUEVA | NUEVA_MARCA

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false, length = 500)
    private String mensaje;

    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();

    @Builder.Default
    private boolean leida = false;
}
