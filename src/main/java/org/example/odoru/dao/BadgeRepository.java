package org.example.odoru.dao;

import org.example.odoru.entities.Badge;
import org.example.odoru.entities.Membre;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface BadgeRepository extends CrudRepository<Badge, Long> {

    Optional<Badge> findByNumeroBadge(String numeroBadge);

    Optional<Badge> findByMembre(Membre membre);
}
