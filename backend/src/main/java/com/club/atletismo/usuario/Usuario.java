package com.club.atletismo.usuario;

import com.club.atletismo.grupo.Grupo;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "usuario")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Usuario implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombreCompleto;

    @Column(nullable = false, unique = true)
    private String correo;

    @Column(nullable = false)
    private String contrasenaHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    private String disciplina;
    private String categoria;
    private String fcmToken;
    private String fotoUrl;

    // Fecha de nacimiento del atleta (para calcular si es menor de edad)
    private LocalDate fechaNacimiento;

    // Datos del tutor de emergencia (obligatorios si el atleta es menor) — protección de datos de menores
    private String tutorNombre;
    private String tutorParentesco;
    private String tutorTelefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grupo_id")
    private Grupo grupo;

    // Vínculo PADRE → atleta (hijo) que el padre/tutor puede observar. Solo aplica a rol PADRE.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "atleta_vinculado_id")
    private Usuario atletaVinculado;

    /** Edad en años a partir de la fecha de nacimiento (null si no hay fecha). */
    @Transient
    public Integer getEdad() {
        return fechaNacimiento != null
                ? Period.between(fechaNacimiento, LocalDate.now()).getYears()
                : null;
    }

    /** True si el atleta es menor de 18 años (mayoría de edad en Bolivia). */
    @Transient
    public boolean isMenorDeEdad() {
        Integer edad = getEdad();
        return edad != null && edad < 18;
    }

    @Builder.Default
    @Column(nullable = false)
    private boolean activo = true;

    @Builder.Default
    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    @Override public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }
    @Override public String getPassword()  { return contrasenaHash; }
    @Override public String getUsername()  { return correo; }
    @Override public boolean isEnabled()   { return activo; }
}
