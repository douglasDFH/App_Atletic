package com.club.atletismo.datosfisicos;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DatosFisicosRepository extends JpaRepository<DatosFisicos, Long> {

    List<DatosFisicos> findByAtletaIdOrderByFechaDesc(Long atletaId);

    Optional<DatosFisicos> findTopByAtletaIdOrderByFechaDesc(Long atletaId);
}
