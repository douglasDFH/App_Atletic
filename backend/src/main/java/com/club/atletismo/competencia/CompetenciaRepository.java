package com.club.atletismo.competencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetenciaRepository extends JpaRepository<Competencia, Long> {

    List<Competencia> findByEstadoOrderByFechaEventoAsc(EstadoCompetencia estado);

    List<Competencia> findAllByOrderByFechaEventoAsc();
}
