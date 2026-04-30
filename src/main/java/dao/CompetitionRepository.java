package dao;

import Entites.Competition;
//
import Entites.Membre;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

/**
 * DAO pour les entités de type Competition
 */

public interface CompetitionRepository extends CrudRepository<Competition, Long> {

    List<Competition> findByDateDebut(LocalDate dateDebut);

    List<Membre> findResultat(Long idCours);

}
