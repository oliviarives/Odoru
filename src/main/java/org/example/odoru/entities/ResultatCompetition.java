package org.example.odoru.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class ResultatCompetition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membre_id")
    private Membre membre;

    @ManyToOne
    @JoinColumn(name = "competition_id")
    private Competition competition;

    private double note;

    public ResultatCompetition(Membre membre, Competition competition, double note) {
        this.membre = membre;
        this.competition = competition;
        this.note = note;
    }
}