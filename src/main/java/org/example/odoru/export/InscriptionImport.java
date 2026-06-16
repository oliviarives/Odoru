package org.example.odoru.export;

public record InscriptionImport(
        String nom,
        String prenom,
        String email,
        String username,
        String password,
        String ville,
        String pays,
        int niveauExpertise
) {}