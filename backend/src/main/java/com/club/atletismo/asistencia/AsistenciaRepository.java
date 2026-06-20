package com.club.atletismo.asistencia;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AsistenciaRepository extends JpaRepository<Asistencia, Long> {

    List<Asistencia> findBySesionId(Long sesionId);

    Optional<Asistencia> findBySesionIdAndAtletaId(Long sesionId, Long atletaId);

    List<Asistencia> findByAtletaIdOrderBySesionHoraInicioDesc(Long atletaId);

    @Query("SELECT a FROM Asistencia a WHERE a.atleta.id = :atletaId AND a.sesion.horaInicio >= :desde ORDER BY a.sesion.horaInicio DESC")
    List<Asistencia> findByAtletaIdDesde(@Param("atletaId") Long atletaId,
                                         @Param("desde") LocalDateTime desde);

    @Query("SELECT a FROM Asistencia a WHERE a.sesion.grupo.id = :grupoId AND a.sesion.horaInicio >= :inicio AND a.sesion.horaInicio < :fin")
    List<Asistencia> findByGrupoAndPeriodo(@Param("grupoId") Long grupoId,
                                           @Param("inicio") LocalDateTime inicio,
                                           @Param("fin") LocalDateTime fin);
}
