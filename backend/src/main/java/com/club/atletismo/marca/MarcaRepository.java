package com.club.atletismo.marca;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarcaRepository extends JpaRepository<MarcaPersonal, Long> {

    List<MarcaPersonal> findByAtletaIdOrderByFechaDesc(Long atletaId);

    List<MarcaPersonal> findByAtletaIdAndDisciplinaOrderByFechaDesc(Long atletaId, String disciplina);

    List<MarcaPersonal> findByDisciplinaOrderByFechaDesc(String disciplina);

    List<MarcaPersonal> findAllByOrderByFechaDesc();

    List<MarcaPersonal> findByAtletaIdAndDisciplinaAndEsMejorMarcaTrue(Long atletaId, String disciplina);
}
