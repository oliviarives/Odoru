package org.example.odoru.export;

import org.example.odoru.entities.Competition;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public record CompetitionExport(
        Long id,
        String titre,
        int niveauCible,
        DayOfWeek jour,
        LocalTime heureDebut,
        String lieu,
        int dureeMinutes,
        LocalDate dateDebut,
        String enseignantNom,
        Long enseignantId
) {
    public static CompetitionExport depuis(Competition competition) {
        Long ensId = competition.getEnseignant() != null ? competition.getEnseignant().getId() : null;
        String ensNom = competition.getEnseignant() != null
                ? competition.getEnseignant().getPrenom() + " " + competition.getEnseignant().getNom()
                : null;
        return new CompetitionExport(
                competition.getId(),
                competition.getTitre(),
                competition.getNiveauCible(),
                competition.getJour(),
                competition.getHeureDebut(),
                competition.getLieu(),
                competition.getDureeMinutes(),
                competition.getDateDebut(),
                ensNom,
                ensId
        );
    }
}