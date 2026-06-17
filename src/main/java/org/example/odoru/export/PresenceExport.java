package org.example.odoru.export;

import org.example.odoru.entities.Presence;

import java.time.LocalDateTime;

public record PresenceExport(
        Long membreId,
        Long coursId,
        String coursTitre,
        LocalDateTime dateHeure
) {
    public static PresenceExport depuis(Presence presence) {
        return new PresenceExport(
                presence.getMembre().getId(),
                presence.getCours().getId(),
                presence.getCours().getTitre(),
                presence.getDateHeure()
        );
    }
}