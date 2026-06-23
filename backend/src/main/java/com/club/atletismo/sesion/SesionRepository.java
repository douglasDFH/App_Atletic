package com.club.atletismo.sesion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SesionRepository extends JpaRepository<SesionEntrenamiento, Long> {

    @Query("SELECT s FROM SesionEntrenamiento s WHERE s.horaInicio >= :inicio AND s.horaInicio < :fin ORDER BY s.horaInicio ASC")
    List<SesionEntrenamiento> findBySemana(@Param("inicio") LocalDateTime inicio,
                                           @Param("fin") LocalDateTime fin);

    @Query("SELECT s FROM SesionEntrenamiento s WHERE s.horaInicio >= :inicio AND s.horaInicio < :fin AND s.grupo.id = :grupoId ORDER BY s.horaInicio ASC")
    List<SesionEntrenamiento> findBySemanaAndGrupo(@Param("inicio") LocalDateTime inicio,
                                                   @Param("fin") LocalDateTime fin,
                                                   @Param("grupoId") Long grupoId);

    List<SesionEntrenamiento> findByGrupoIdOrderByHoraInicioDesc(Long grupoId);

    @Query("SELECT COUNT(s) FROM SesionEntrenamiento s " +
           "WHERE s.grupo.id = :grupoId " +
           "AND s.estado <> com.club.atletismo.sesion.EstadoSesion.CANCELADA " +
           "AND s.horaInicio < :fin AND s.horaFin > :inicio " +
           "AND s.id <> :excludeId")
    long countConflictos(@Param("grupoId") Long grupoId,
                         @Param("inicio") LocalDateTime inicio,
                         @Param("fin") LocalDateTime fin,
                         @Param("excludeId") Long excludeId);
}
