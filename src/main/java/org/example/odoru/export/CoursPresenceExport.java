package org.example.odoru.export;

import java.time.LocalDate;

public record CoursPresenceExport(
        Long coursId,
        String coursTitre,
        LocalDate dateCours,
        boolean present
) {}