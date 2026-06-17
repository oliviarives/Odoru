package org.example.odoru.export;

import org.example.odoru.entities.ResultatCompetition;

public record ResultatExport(
        Long competitionId,
        String competitionTitre,
        Long eleveId,
        String eleveNom,
        double note
) {
    public static ResultatExport depuis(ResultatCompetition resultat) {
        return new ResultatExport(
                resultat.getCompetition().getId(),
                resultat.getCompetition().getTitre(),
                resultat.getMembre().getId(),
                resultat.getMembre().getPrenom() + " " + resultat.getMembre().getNom(),
                resultat.getNote()
        );
    }
}