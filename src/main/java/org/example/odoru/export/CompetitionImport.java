package org.example.odoru.export;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public record CompetitionImport(
        String titre,
        int niveauCible,
        DayOfWeek jour,
        LocalTime heureDebut,
        String lieu,
        int dureeMinutes,
        LocalDate dateDebut
) {}