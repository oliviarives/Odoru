package org.example.odoru.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Badge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroBadge;

    @OneToOne
    @JoinColumn(name = "membre_id", unique = true)
    private Membre membre;

    public Badge(String numeroBadge) {
        this.numeroBadge = numeroBadge;
    }
}