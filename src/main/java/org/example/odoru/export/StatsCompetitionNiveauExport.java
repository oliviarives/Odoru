package org.example.odoru.export;

import java.util.List;

public record StatsCompetitionNiveauExport(
        int niveauCible,
        long nombreCompetitions,
        List<CompetitionExport> competitions
) {}