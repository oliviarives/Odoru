package org.example.odoru.metier;


import org.example.odoru.dao.MembreRepository;
import org.example.odoru.entities.EtatInscription;
import org.example.odoru.entities.Membre;
import org.example.odoru.entities.Role;
import org.example.odoru.exceptions.EmailAlreadyExistException;
import org.example.odoru.exceptions.NiveauInvalideException;
import org.example.odoru.exceptions.UsernameAlreadyExistException;
import org.example.odoru.export.InscriptionImport;
import org.example.odoru.export.MembreExport;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class ServiceAuth {

    private final MembreRepository membreRepository;
    private final PasswordEncoder passwordEncoder;

    public ServiceAuth(MembreRepository membreRepository, PasswordEncoder passwordEncoder) {
        this.membreRepository = membreRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public MembreExport inscrire(InscriptionImport inscription) {
        if (inscription.niveauExpertise() < 1 || inscription.niveauExpertise() > 5) {
            throw new NiveauInvalideException("Le niveau d'expertise doit être compris entre 1 et 5.");
        }
        if (membreRepository.existsByUsername(inscription.username())) {
            throw new UsernameAlreadyExistException("Le nom d'utilisateur '" + inscription.username() + "' est déjà utilisé.");
        }
        if (membreRepository.existsByEmail(inscription.email())) {
            throw new EmailAlreadyExistException("L'email '" + inscription.email() + "' est déjà utilisé.");
        }

        Membre membre = new Membre(
                inscription.nom(),
                inscription.prenom(),
                inscription.email(),
                inscription.username(),
                passwordEncoder.encode(inscription.password()),
                inscription.ville(),
                inscription.pays(),
                inscription.niveauExpertise(),
                Role.ELEVE,
                EtatInscription.EN_ATTENTE,
                LocalDate.now()
        );
        return MembreExport.depuis(membreRepository.save(membre));
    }
}
