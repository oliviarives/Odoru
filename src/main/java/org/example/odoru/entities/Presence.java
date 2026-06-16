package org.example.odoru.entities;

import jakarta.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class Presence {
    @Id
    public Long Id;

    /*@DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate date;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    LocalTime time;*/

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    LocalDateTime dateTime;
}
