package org.example.odoru.export;

import org.example.odoru.entities.Presence;

import java.time.LocalDate;
import java.time.LocalDateTime;

public record HistoriqueExport(
        Long coursId,
        String coursTitre,
        LocalDate dateCours,
        LocalDateTime dateHeurePresence
) {
    public static HistoriqueExport depuis(Presence presence) {
        return new HistoriqueExport(
                presence.getCours().getId(),
                presence.getCours().getTitre(),
                presence.getCours().getDateDebut(),
                presence.getDateHeure()
        );
    }
}