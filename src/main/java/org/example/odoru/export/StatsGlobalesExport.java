package org.example.odoru.export;

import java.util.List;

public record StatsGlobalesExport(
        long nombreCours,
        double moyenneElevesPresents,
        List<StatCoursExport> details
) {
    public record StatCoursExport(
            Long coursId,
            String coursTitre,
            long nombrePresents
    ) {}
}