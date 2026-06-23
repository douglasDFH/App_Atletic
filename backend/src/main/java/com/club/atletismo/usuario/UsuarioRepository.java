package com.club.atletismo.usuario;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);
    boolean existsByCorreo(String correo);
    List<Usuario> findByRol(Rol rol);
    List<Usuario> findByRolAndActivo(Rol rol, boolean activo);

    // Cuenta PADRE vinculada a un atleta (hijo)
    Optional<Usuario> findFirstByAtletaVinculadoId(Long atletaId);

    Optional<Usuario> findByTokenVerificacion(String tokenVerificacion);
}
