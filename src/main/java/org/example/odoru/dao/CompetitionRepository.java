package org.example.odoru.dao;

import org.example.odoru.entities.Competition;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CompetitionRepository extends CrudRepository<Competition, Long> {

    List<Competition> findByNiveauCible(int niveauCible);
}