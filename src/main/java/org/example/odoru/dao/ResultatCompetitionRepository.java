package org.example.odoru.dao;

import org.example.odoru.entities.Competition;
import org.example.odoru.entities.Membre;
import org.example.odoru.entities.ResultatCompetition;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface ResultatCompetitionRepository extends CrudRepository<ResultatCompetition, Long> {

    Optional<ResultatCompetition> findByMembreAndCompetition(Membre membre, Competition competition);

    List<ResultatCompetition> findByMembreId(Long membreId);

    List<ResultatCompetition> findByCompetitionId(Long competitionId);
}