package com.club.atletismo.competencia;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultadoRepository extends JpaRepository<ResultadoCompetencia, Long> {
    List<ResultadoCompetencia> findByCompetenciaIdOrderByPosicionAsc(Long competenciaId);
    Optional<ResultadoCompetencia> findByCompetenciaIdAndAtletaId(Long competenciaId, Long atletaId);
}
