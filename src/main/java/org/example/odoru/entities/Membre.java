package org.example.odoru.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Membre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(unique = true, nullable = false)
    private String username;

    private String password;

    private String ville;
    private String pays;

    private int niveauExpertise;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private EtatInscription etatInscription;

    private LocalDate dateInscription;

    public Membre(String nom, String prenom, String email, String username, String password,
                  String ville, String pays, int niveauExpertise, Role role,
                  EtatInscription etatInscription, LocalDate dateInscription) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.username = username;
        this.password = password;
        this.ville = ville;
        this.pays = pays;
        this.niveauExpertise = niveauExpertise;
        this.role = role;
        this.etatInscription = etatInscription;
        this.dateInscription = dateInscription;
    }
}