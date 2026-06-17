package org.example.odoru.dao;

import org.example.odoru.entities.Cours;
import org.example.odoru.entities.Membre;
import org.example.odoru.entities.Presence;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PresenceRepository extends CrudRepository<Presence, Long> {

    boolean existsByMembreAndCours(Membre membre, Cours cours);

    List<Presence> findByMembreId(Long membreId);

    List<Presence> findByCoursId(Long coursId);
}