package Entites;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class Membre {
    @Id
    @GeneratedValue 
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
