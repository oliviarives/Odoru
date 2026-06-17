package org.example.odoru.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Presence {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membre_id")
    private Membre membre;

    @ManyToOne
    @JoinColumn(name = "cours_id")
    private Cours cours;

    private LocalDateTime dateHeure;

    public Presence(Membre membre, Cours cours, LocalDateTime dateHeure) {
        this.membre = membre;
        this.cours = cours;
        this.dateHeure = dateHeure;
    }
}