package dao;

import Entites.Evenement;
import org.springframework.data.repository.CrudRepository;

/**
 * DAO pour les entités de type Evenement
 */

public interface EvenementRepository extends CrudRepository<Evenement, Long> {
}
