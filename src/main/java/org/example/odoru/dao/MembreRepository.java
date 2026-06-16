package org.example.odoru.dao;

import org.example.odoru.entities.Membre;
import org.example.odoru.entities.Role;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface MembreRepository extends CrudRepository<Membre, Long> {

    Optional<Membre> findByUsername(String username);

    Optional<Membre> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    List<Membre> findByRole(Role role);

    List<Membre> findByRoleAndNiveauExpertise(Role role, int niveauExpertise);
}