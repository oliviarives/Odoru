package dao;

import Entites.Cours;
import Entites.Membre;
import Entites.Presence;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * DAO pour les entités de type Cours
 */

public interface CoursRepository extends CrudRepository<Cours, Long> {

    List<Cours> findByEnseignantId(Long idMembre);

    List<Membre> findElevesInscritsByCoursId(Long idCours);

}
