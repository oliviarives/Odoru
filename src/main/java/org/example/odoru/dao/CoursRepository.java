package org.example.odoru.dao;

import org.example.odoru.entities.Cours;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CoursRepository extends CrudRepository<Cours, Long> {

    List<Cours> findByNiveauCible(int niveauCible);

    List<Cours> findByEnseignantId(Long enseignantId);

    List<Cours> findByNiveauCibleAndJour(int niveauCible, java.time.DayOfWeek jour);

    boolean existsByLieuAndJourAndHeureDebut(String lieu, java.time.DayOfWeek jour, java.time.LocalTime heureDebut);
}