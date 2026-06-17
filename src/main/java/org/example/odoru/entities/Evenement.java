package org.example.odoru.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
public abstract class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;

    private int niveauCible;

    @Enumerated(EnumType.STRING)
    private DayOfWeek jour;

    private LocalTime heureDebut;

    private String lieu;

    private int dureeMinutes;

    private LocalDate dateDebut;

    @ManyToOne
    @JoinColumn(name = "enseignant_id")
    private Membre enseignant;

    public Evenement(String titre, int niveauCible, DayOfWeek jour, LocalTime heureDebut,
                     String lieu, int dureeMinutes, LocalDate dateDebut, Membre enseignant) {
        this.titre = titre;
        this.niveauCible = niveauCible;
        this.jour = jour;
        this.heureDebut = heureDebut;
        this.lieu = lieu;
        this.dureeMinutes = dureeMinutes;
        this.dateDebut = dateDebut;
        this.enseignant = enseignant;
    }
}