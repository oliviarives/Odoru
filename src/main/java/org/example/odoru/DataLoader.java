package org.example.odoru;

import org.example.odoru.dao.MembreRepository;
import org.example.odoru.entities.EtatInscription;
import org.example.odoru.entities.Membre;
import org.example.odoru.entities.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataLoader implements CommandLineRunner {

    private final MembreRepository membreRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(MembreRepository membreRepository, PasswordEncoder passwordEncoder) {
        this.membreRepository = membreRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        creer("president", "pres123",   Role.PRESIDENT,  5, "Diallo",  "Awa");
        creer("secretaire","secret123", Role.SECRETAIRE, 1, "Martin",  "Léa");
        creer("prof",      "prof123",   Role.ENSEIGNANT, 5, "Nguyen",  "Hugo");
        creer("eleve",     "eleve123",  Role.ELEVE,      3, "Petit",   "Sami");
    }

    private void creer(String username, String motDePasse, Role role,
                       int niveau, String nom, String prenom) {
        if (membreRepository.existsByUsername(username)) return; // sécurité si jamais

        Membre m = new Membre(
                nom, prenom,
                username + "@odoru.fr",
                username,
                passwordEncoder.encode(motDePasse),   // <-- on hash, sinon le login échoue
                "Toulouse", "France",
                niveau,
                role,
                EtatInscription.ACTIVE,
                LocalDate.now()
        );
        membreRepository.save(m);
    }
}