package org.example.odoru.export;

import org.example.odoru.entities.EtatInscription;
import org.example.odoru.entities.Membre;
import org.example.odoru.entities.Role;

import java.time.LocalDate;

public record MembreExport(
        Long id,
        String nom,
        String prenom,
        String email,
        String username,
        String ville,
        String pays,
        int niveauExpertise,
        Role role,
        EtatInscription etatInscription,
        LocalDate dateInscription
) {
    public static MembreExport depuis(Membre membre) {
        return new MembreExport(
                membre.getId(),
                membre.getNom(),
                membre.getPrenom(),
                membre.getEmail(),
                membre.getUsername(),
                membre.getVille(),
                membre.getPays(),
                membre.getNiveauExpertise(),
                membre.getRole(),
                membre.getEtatInscription(),
                membre.getDateInscription()
        );
    }
}