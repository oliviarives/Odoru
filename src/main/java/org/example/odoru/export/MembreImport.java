package org.example.odoru.export;

import org.example.odoru.entities.Role;

public record MembreImport(
        String nom,
        String prenom,
        String email,
        String username,
        String password,
        String ville,
        String pays,
        int niveauExpertise,
        Role role
) {}