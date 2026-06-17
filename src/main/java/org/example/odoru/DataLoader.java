package org.example.odoru;

import org.example.odoru.dao.BadgeRepository;
import org.example.odoru.dao.CompetitionRepository;
import org.example.odoru.dao.CoursRepository;
import org.example.odoru.dao.MembreRepository;
import org.example.odoru.dao.PresenceRepository;
import org.example.odoru.dao.ResultatCompetitionRepository;
import org.example.odoru.entities.Badge;
import org.example.odoru.entities.Competition;
import org.example.odoru.entities.Cours;
import org.example.odoru.entities.EtatInscription;
import org.example.odoru.entities.Membre;
import org.example.odoru.entities.Presence;
import org.example.odoru.entities.ResultatCompetition;
import org.example.odoru.entities.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Component
public class DataLoader implements CommandLineRunner {

    private final MembreRepository membreRepository;
    private final BadgeRepository badgeRepository;
    private final CoursRepository coursRepository;
    private final PresenceRepository presenceRepository;
    private final CompetitionRepository competitionRepository;
    private final ResultatCompetitionRepository resultatCompetitionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataLoader(MembreRepository membreRepository, BadgeRepository badgeRepository,
                      CoursRepository coursRepository, PresenceRepository presenceRepository,
                      CompetitionRepository competitionRepository,
                      ResultatCompetitionRepository resultatCompetitionRepository,
                      PasswordEncoder passwordEncoder) {
        this.membreRepository = membreRepository;
        this.badgeRepository = badgeRepository;
        this.coursRepository = coursRepository;
        this.presenceRepository = presenceRepository;
        this.competitionRepository = competitionRepository;
        this.resultatCompetitionRepository = resultatCompetitionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        Membre president = creer("president", "pres123", Role.PRESIDENT, 5, "Diallo", "Awa", EtatInscription.ACTIVE);
        Membre secretaire = creer("secretaire", "secret123", Role.SECRETAIRE, 1, "Martin", "Lea", EtatInscription.ACTIVE);
        Membre prof = creer("prof", "prof123", Role.ENSEIGNANT, 5, "Nguyen", "Hugo", EtatInscription.ACTIVE);
        Membre prof2 = creer("prof.maya", "prof123", Role.ENSEIGNANT, 4, "Rousseau", "Maya", EtatInscription.ACTIVE);
        Membre eleve = creer("eleve", "eleve123", Role.ELEVE, 3, "Petit", "Sami", EtatInscription.ACTIVE);

        List<Membre> eleves = List.of(
                eleve,
                creer("ines", "eleve123", Role.ELEVE, 1, "Benali", "Ines", EtatInscription.ACTIVE),
                creer("noa", "eleve123", Role.ELEVE, 2, "Morel", "Noa", EtatInscription.ACTIVE),
                creer("jade", "eleve123", Role.ELEVE, 2, "Leroy", "Jade", EtatInscription.EN_ATTENTE),
                creer("malo", "eleve123", Role.ELEVE, 3, "Garnier", "Malo", EtatInscription.ACTIVE),
                creer("zoe", "eleve123", Role.ELEVE, 3, "Faure", "Zoe", EtatInscription.ACTIVE),
                creer("adam", "eleve123", Role.ELEVE, 4, "Mercier", "Adam", EtatInscription.ACTIVE),
                creer("lina", "eleve123", Role.ELEVE, 4, "Robin", "Lina", EtatInscription.SUSPENDUE),
                creer("yanis", "eleve123", Role.ELEVE, 5, "Roux", "Yanis", EtatInscription.ACTIVE),
                creer("emma", "eleve123", Role.ELEVE, 5, "Girard", "Emma", EtatInscription.ACTIVE)
        );

        associerBadgeDemo(eleve);
        associerBadgesDemo(eleves);
        creerJeuDemo(prof, prof2, eleves);
        creerCoursBadgeageDemo(prof, eleve.getNiveauExpertise());
    }

    private Membre creer(String username, String motDePasse, Role role,
                         int niveau, String nom, String prenom, EtatInscription etat) {
        if (membreRepository.existsByUsername(username)) {
            return membreRepository.findByUsername(username).orElseThrow();
        }

        Membre m = new Membre(
                nom, prenom,
                username + "@odoru.fr",
                username,
                passwordEncoder.encode(motDePasse),   // <-- on hash, sinon le login échoue
                "Toulouse", "France",
                niveau,
                role,
                etat,
                LocalDate.now()
        );
        return membreRepository.save(m);
    }

    private void associerBadgeDemo(Membre eleve) {
        if (badgeRepository.findByMembre(eleve).isPresent()) return;

        Badge badge = new Badge("BADGE-SAMI-001");
        badge.setMembre(eleve);
        badgeRepository.save(badge);
    }

    private void associerBadgesDemo(List<Membre> eleves) {
        for (Membre eleve : eleves) {
            String numero = "BADGE-" + eleve.getUsername().toUpperCase().replace(".", "-") + "-001";
            if (badgeRepository.findByNumeroBadge(numero).isPresent() || badgeRepository.findByMembre(eleve).isPresent()) continue;
            Badge badge = new Badge(numero);
            badge.setMembre(eleve);
            badgeRepository.save(badge);
        }
    }

    private void creerCoursBadgeageDemo(Membre prof, int niveauEleve) {
        LocalDateTime maintenant = LocalDateTime.now();
        LocalTime debut = maintenant.toLocalTime().minusMinutes(30).withSecond(0).withNano(0);

        creerCoursSiAbsent(
                "Cours courant - test badgeage",
                niveauEleve,
                maintenant.getDayOfWeek(),
                debut,
                "Salle CallMe",
                120,
                maintenant.toLocalDate(),
                prof
        );
    }

    private void creerJeuDemo(Membre prof, Membre prof2, List<Membre> eleves) {
        LocalDate today = LocalDate.now();
        List<Cours> cours = List.of(
                creerCoursSiAbsent("Fondations niveau 1", 1, DayOfWeek.MONDAY, LocalTime.of(17, 30), "Studio A", 75, today.minusDays(16), prof2),
                creerCoursSiAbsent("Technique niveau 2", 2, DayOfWeek.TUESDAY, LocalTime.of(18, 0), "Studio B", 90, today.minusDays(14), prof),
                creerCoursSiAbsent("Atelier musicalite", 3, DayOfWeek.WEDNESDAY, LocalTime.of(19, 0), "Salle CallMe", 75, today.minusDays(9), prof),
                creerCoursSiAbsent("Flow avance", 4, DayOfWeek.THURSDAY, LocalTime.of(19, 30), "Studio A", 90, today.minusDays(7), prof2),
                creerCoursSiAbsent("Training competition", 5, DayOfWeek.SATURDAY, LocalTime.of(10, 30), "Grande salle", 120, today.minusDays(4), prof),
                creerCoursSiAbsent("Cours niveau 3 - semaine prochaine", 3, DayOfWeek.WEDNESDAY, LocalTime.of(18, 30), "Salle CallMe", 75, today.plusDays(8), prof),
                creerCoursSiAbsent("Cours niveau 4 - intensif", 4, DayOfWeek.FRIDAY, LocalTime.of(18, 45), "Studio B", 90, today.plusDays(10), prof2)
        );

        for (int i = 0; i < cours.size(); i++) {
            Cours c = cours.get(i);
            for (Membre eleve : eleves) {
                if (eleve.getRole() == Role.ELEVE && eleve.getNiveauExpertise() >= c.getNiveauCible() - 1 && (eleve.getId() + i) % 3 != 0) {
                    ajouterPresence(eleve, c, c.getDateDebut().atTime(c.getHeureDebut()).plusMinutes(7 + i));
                }
            }
        }

        Competition departementale = creerCompetitionSiAbsente("Challenge departemental", 3, DayOfWeek.SUNDAY, LocalTime.of(14, 0), "Gymnase central", 180, today.minusDays(11), prof);
        Competition regionale = creerCompetitionSiAbsente("Coupe regionale", 4, DayOfWeek.SUNDAY, LocalTime.of(13, 30), "Palais des sports", 210, today.plusDays(21), prof2);
        Competition interne = creerCompetitionSiAbsente("Rencontre interne Odoru", 2, DayOfWeek.SATURDAY, LocalTime.of(15, 0), "Salle CallMe", 150, today.minusDays(2), prof);

        ajouterResultat(eleves.get(0), departementale, 16.5);
        ajouterResultat(eleves.get(4), departementale, 14.0);
        ajouterResultat(eleves.get(5), departementale, 17.25);
        ajouterResultat(eleves.get(6), regionale, 15.75);
        ajouterResultat(eleves.get(8), regionale, 18.0);
        ajouterResultat(eleves.get(2), interne, 13.5);
        ajouterResultat(eleves.get(3), interne, 12.0);
    }

    private Cours creerCoursSiAbsent(String titre, int niveauCible, DayOfWeek jour, LocalTime heureDebut,
                                     String lieu, int dureeMinutes, LocalDate dateDebut, Membre enseignant) {
        for (Cours cours : coursRepository.findAll()) {
            if (cours.getTitre().equals(titre) && cours.getDateDebut().equals(dateDebut)) {
                return cours;
            }
        }
        return coursRepository.save(new Cours(titre, niveauCible, jour, heureDebut, lieu, dureeMinutes, dateDebut, enseignant));
    }

    private Competition creerCompetitionSiAbsente(String titre, int niveauCible, DayOfWeek jour, LocalTime heureDebut,
                                                  String lieu, int dureeMinutes, LocalDate dateDebut, Membre enseignant) {
        for (Competition competition : competitionRepository.findAll()) {
            if (competition.getTitre().equals(titre) && competition.getDateDebut().equals(dateDebut)) {
                return competition;
            }
        }
        return competitionRepository.save(new Competition(titre, niveauCible, jour, heureDebut, lieu, dureeMinutes, dateDebut, enseignant));
    }

    private void ajouterPresence(Membre membre, Cours cours, LocalDateTime dateHeure) {
        if (!presenceRepository.existsByMembreAndCours(membre, cours)) {
            presenceRepository.save(new Presence(membre, cours, dateHeure));
        }
    }

    private void ajouterResultat(Membre membre, Competition competition, double note) {
        if (resultatCompetitionRepository.findByMembreAndCompetition(membre, competition).isEmpty()) {
            resultatCompetitionRepository.save(new ResultatCompetition(membre, competition, note));
        }
    }
}
