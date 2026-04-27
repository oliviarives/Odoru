package Entites;

import jakarta.persistence.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

public class Membre {
    @Id
    public Long Id;

    public String nom;
    public String prenom;
    public String email;
    public String username;
    public String password;
    public String ville;
    public String pays;
    public int niveauExpertise;
    public Role role;
    public EtatInscription etatInscription;

    @DateTimeFormat
    public LocalDateTime Localdate;

}
