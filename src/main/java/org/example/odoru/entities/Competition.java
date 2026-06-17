package org.example.odoru.entities;

import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Competition extends Evenement {

    public Competition(String titre, int niveauCible, DayOfWeek jour, LocalTime heureDebut,
                       String lieu, int dureeMinutes, LocalDate dateDebut, Membre enseignant) {
        super(titre, niveauCible, jour, heureDebut, lieu, dureeMinutes, dateDebut, enseignant);
    }
}