package dao;

import Entites.ResultatCompetition;
import org.springframework.data.repository.CrudRepository;

/**
 * DAO pour les entités de type ResultatCompetition
 */

public interface ResultatCompetitionRepository extends CrudRepository<ResultatCompetition, Long> {
}
