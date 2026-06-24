package com.club.atletismo.marca;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MarcaRepository extends JpaRepository<MarcaPersonal, Long> {

    List<MarcaPersonal> findByAtletaIdOrderByFechaDesc(Long atletaId);

    List<MarcaPersonal> findByAtletaIdAndDisciplinaOrderByFechaDesc(Long atletaId, String disciplina);

    List<MarcaPersonal> findByDisciplinaOrderByFechaDesc(String disciplina);

    List<MarcaPersonal> findAllByOrderByFechaDesc();

    List<MarcaPersonal> findByAtletaIdAndDisciplinaAndEsMejorMarcaTrue(Long atletaId, String disciplina);

    @org.springframework.data.jpa.repository.Query(
        "SELECT m FROM MarcaPersonal m WHERE m.atleta.grupo.id = :grupoId " +
        "AND m.disciplina = :disciplina ORDER BY m.atleta.id ASC, m.fecha ASC")
    List<MarcaPersonal> findByGrupoIdAndDisciplina(
        @org.springframework.data.repository.query.Param("grupoId") Long grupoId,
        @org.springframework.data.repository.query.Param("disciplina") String disciplina);

    @org.springframework.data.jpa.repository.Query(
        "SELECT m FROM MarcaPersonal m WHERE m.atleta.grupo.id = :grupoId " +
        "ORDER BY m.atleta.id ASC, m.fecha ASC")
    List<MarcaPersonal> findByGrupoId(
        @org.springframework.data.repository.query.Param("grupoId") Long grupoId);
}
