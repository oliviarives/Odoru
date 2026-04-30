package dao;

import Entites.EtatInscription;
import Entites.Membre;
import Entites.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

/**
 * DAO pour les entités de type Membre
 */

public interface MembreRepository extends CrudRepository<Membre, Long> {


    List<Membre> findByNomAndPrenom(String nom, String prenom);

    List<Membre> findByRole(Role role);

    List<Membre> findByEtatInscription(EtatInscription etatInscription);

    Optional<Membre> findByUsername(String username);

    Optional<Membre> findByEmail(String email);
}
