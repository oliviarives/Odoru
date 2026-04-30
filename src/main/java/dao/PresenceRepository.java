package dao;

import Entites.EtatInscription;
import Entites.Membre;
import Entites.Presence;
import Entites.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * DAO pour les entités de type Presence
 */

public interface PresenceRepository extends CrudRepository<Presence, Long> {

    List<Presence> findByMembreId(Long idMembre);

    List<Presence> findByCoursId(Long idCours);

    Optional <Presence> findByMembreIdAndCoursId(Long idMembre, Long idCours);

}
