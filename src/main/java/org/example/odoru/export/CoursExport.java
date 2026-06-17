package org.example.odoru.export;

import org.example.odoru.entities.Cours;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public record CoursExport(
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
    public static CoursExport depuis(Cours cours) {
        Long ensId = cours.getEnseignant() != null ? cours.getEnseignant().getId() : null;
        String ensNom = cours.getEnseignant() != null
                ? cours.getEnseignant().getPrenom() + " " + cours.getEnseignant().getNom()
                : null;
        return new CoursExport(
                cours.getId(),
                cours.getTitre(),
                cours.getNiveauCible(),
                cours.getJour(),
                cours.getHeureDebut(),
                cours.getLieu(),
                cours.getDureeMinutes(),
                cours.getDateDebut(),
                ensNom,
                ensId
        );
    }
}