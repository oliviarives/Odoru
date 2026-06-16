package org.example.odoru.entities;

import jakarta.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

public class Evenement {
    @Id
    public Long Id;

    public String titre;
    public int niveauCible;
    public DayOfWeek jour;

    @DateTimeFormat(iso = DateTimeFormat.ISO.TIME)
    LocalTime heureDebut;

    public String lieu;
    public int dureeMinutes;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    LocalDate dateDebut;
}
