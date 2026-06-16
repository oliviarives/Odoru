package org.example.odoru.metier;

import org.example.odoru.dao.MembreRepository;
import org.example.odoru.entities.EtatInscription;
import org.example.odoru.entities.Membre;
import org.example.odoru.entities.Role;
import org.example.odoru.exceptions.*;
import org.example.odoru.export.MembreExport;
import org.example.odoru.export.MembreImport;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceMembre {

    private final MembreRepository membreRepository;
    private final PasswordEncoder passwordEncoder;

    public ServiceMembre(MembreRepository membreRepository, PasswordEncoder passwordEncoder) {
        this.membreRepository = membreRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public Membre recupererMembre(long id) {
        return membreRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("Aucun membre avec l'id " + id));
    }

    public MembreExport consulterMembre(long id) {
        return MembreExport.depuis(recupererMembre(id));
    }

    public List<MembreExport> listerMembres() {
        List<MembreExport> resultat = new ArrayList<>();
        membreRepository.findAll().forEach(m -> resultat.add(MembreExport.depuis(m)));
        return resultat;
    }

    public MembreExport creerMembre(MembreImport membreImport) {
        verifierUnicite(membreImport.username(), membreImport.email());
        verifierNiveau(membreImport.niveauExpertise());

        Membre membre = new Membre(
                membreImport.nom(),
                membreImport.prenom(),
                membreImport.email(),
                membreImport.username(),
                passwordEncoder.encode(membreImport.password()),
                membreImport.ville(),
                membreImport.pays(),
                membreImport.niveauExpertise(),
                membreImport.role() != null ? membreImport.role() : Role.ELEVE,
                EtatInscription.ACTIVE,
                LocalDate.now()
        );
        return MembreExport.depuis(membreRepository.save(membre));
    }

    public MembreExport modifierNiveau(long id, int niveauExpertise) {
        verifierNiveau(niveauExpertise);
        Membre membre = recupererMembre(id);
        membre.setNiveauExpertise(niveauExpertise);
        return MembreExport.depuis(membreRepository.save(membre));
    }

    public MembreExport modifierEtatInscription(long id, EtatInscription etat) {
        if (etat == null) {
            throw new EtatInvalideException("L'état d'inscription est obligatoire.");
        }
        Membre membre = recupererMembre(id);
        membre.setEtatInscription(etat);
        return MembreExport.depuis(membreRepository.save(membre));
    }

    public EtatInscription consulterEtatInscription(long id) {
        return recupererMembre(id).getEtatInscription();
    }

    // ----------------- Helpers de validation -----------------

    void verifierUnicite(String username, String email) {
        if (membreRepository.existsByUsername(username)) {
            throw new UsernameAlreadyExistException("Le nom d'utilisateur '" + username + "' est déjà utilisé.");
        }
        if (membreRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistException("L'email '" + email + "' est déjà utilisé.");
        }
    }

    void verifierNiveau(int niveau) {
        if (niveau < 1 || niveau > 5) {
            throw new NiveauInvalideException("Le niveau d'expertise doit être compris entre 1 et 5.");
        }
    }
}
