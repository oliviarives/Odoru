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
        LocalDate today = LocalDate.now();

        // ---------- 1. MEMBRES (ids 1..14 dans cet ordre) ----------
        Membre president  = creer("president",  "pres123",   Role.PRESIDENT,  5, "Diallo",  "Awa",  EtatInscription.ACTIVE);
        Membre secretaire = creer("secretaire", "secret123", Role.SECRETAIRE, 1, "Martin",  "Lea",  EtatInscription.ACTIVE);
        Membre prof       = creer("prof",       "prof123",   Role.ENSEIGNANT, 5, "Nguyen",  "Hugo", EtatInscription.ACTIVE);
        Membre profMaya   = creer("prof.maya",  "prof123",   Role.ENSEIGNANT, 4, "Rousseau","Maya", EtatInscription.ACTIVE);

        Membre sami  = creer("sami",  "eleve123", Role.ELEVE, 3, "Petit",   "Sami",  EtatInscription.ACTIVE);     // id 5  -> demo badge
        Membre ines  = creer("ines",  "eleve123", Role.ELEVE, 1, "Benali",  "Ines",  EtatInscription.ACTIVE);     // id 6
        Membre noa   = creer("noa",   "eleve123", Role.ELEVE, 2, "Morel",   "Noa",   EtatInscription.ACTIVE);     // id 7
        Membre jade  = creer("jade",  "eleve123", Role.ELEVE, 2, "Leroy",   "Jade",  EtatInscription.EN_ATTENTE); // id 8  -> demo inscription
        Membre malo  = creer("malo",  "eleve123", Role.ELEVE, 3, "Garnier", "Malo",  EtatInscription.ACTIVE);     // id 9
        Membre zoe   = creer("zoe",   "eleve123", Role.ELEVE, 3, "Faure",   "Zoe",   EtatInscription.ACTIVE);     // id 10
        Membre adam  = creer("adam",  "eleve123", Role.ELEVE, 4, "Mercier", "Adam",  EtatInscription.ACTIVE);     // id 11
        Membre lina  = creer("lina",  "eleve123", Role.ELEVE, 4, "Robin",   "Lina",  EtatInscription.SUSPENDUE);  // id 12
        Membre yanis = creer("yanis", "eleve123", Role.ELEVE, 5, "Roux",    "Yanis", EtatInscription.ACTIVE);     // id 13
        Membre emma  = creer("emma",  "eleve123", Role.ELEVE, 5, "Girard",  "Emma",  EtatInscription.ACTIVE);     // id 14 -> demo associer badge (PAS de badge)

        // ---------- 2. BADGES ----------
        // On donne un badge a la plupart des eleves...
        associerBadge(sami,  "BADGE-SAMI-001");
        associerBadge(ines,  "BADGE-INES-001");
        associerBadge(noa,   "BADGE-NOA-001");
        associerBadge(malo,  "BADGE-MALO-001");
        associerBadge(zoe,   "BADGE-ZOE-001");
        associerBadge(adam,  "BADGE-ADAM-001");
        associerBadge(yanis, "BADGE-YANIS-001");
        // ...mais PAS a Emma (id 14) : reserve pour la demo "associer un badge" en live.
        // Un badge libre, non associe a un membre : pour la demo du cas d'erreur "badge sans membre".
        creerBadgeLibre("BADGE-LIBRE-000");

        // ---------- 3. COURS (ids evenement 1..7) ----------
        // id 1 : COURS EN COURS aujourd'hui, niveau 3 -> permet le badgeage LIVE de Sami (niveau 3).
        LocalTime debutCourant = LocalTime.now().minusMinutes(15).withSecond(0).withNano(0);
        Cours coursCourant = creerCours("Cours en cours (demo badge)", 3, today.getDayOfWeek(),
                debutCourant, "Salle CallMe", 120, today, prof);

        Cours c1 = creerCours("Fondations niveau 1",  1, DayOfWeek.MONDAY,    LocalTime.of(17, 30), "Studio A",      75, today.minusDays(16), profMaya); // id 2
        Cours c2 = creerCours("Technique niveau 2",   2, DayOfWeek.TUESDAY,   LocalTime.of(18, 0),  "Studio B",      90, today.minusDays(14), prof);     // id 3
        Cours c3 = creerCours("Atelier musicalite",   3, DayOfWeek.WEDNESDAY, LocalTime.of(19, 0),  "Salle CallMe",  75, today.minusDays(9),  prof);     // id 4
        Cours c4 = creerCours("Flow avance",          4, DayOfWeek.THURSDAY,  LocalTime.of(19, 30), "Studio A",      90, today.minusDays(7),  profMaya); // id 5
        Cours c5 = creerCours("Training competition", 5, DayOfWeek.SATURDAY,  LocalTime.of(10, 30), "Grande salle", 120, today.minusDays(4),  prof);     // id 6
        creerCours("Cours niveau 3 - semaine prochaine", 3, DayOfWeek.WEDNESDAY, LocalTime.of(18, 30), "Salle CallMe", 75, today.plusDays(8), prof);     // id 7

        // ---------- 4. PRESENCES (sur des cours PASSES ; jamais sur le cours en cours) ----------
        presence(sami, c3, c3);   // Sami present a un cours niveau 3 passe
        presence(malo, c3, c3);
        presence(zoe,  c3, c3);
        presence(ines, c1, c1);
        presence(noa,  c2, c2);
        presence(adam, c4, c4);
        presence(yanis, c5, c5);
        presence(zoe,  c5, c5);

        // ---------- 5. COMPETITIONS (ids evenement 8..10) ----------
        Competition departementale = creerCompetition("Challenge departemental", 3, DayOfWeek.SUNDAY, LocalTime.of(14, 0), "Gymnase central",   180, today.minusDays(11), prof);     // id 8
        creerCompetition("Coupe regionale", 4, DayOfWeek.SUNDAY, LocalTime.of(13, 30), "Palais des sports", 210, today.plusDays(21), profMaya);                                       // id 9 (a venir)
        Competition interne = creerCompetition("Rencontre interne Odoru", 2, DayOfWeek.SATURDAY, LocalTime.of(15, 0), "Salle CallMe", 150, today.minusDays(2), prof);                  // id 10

        // ---------- 6. RESULTATS (notes valides : 0..10, au 1/10e) ----------
        resultat(sami, departementale, 8.5);
        resultat(malo, departementale, 7.0);
        resultat(zoe,  departementale, 9.0);
        resultat(noa,  interne, 7.5);
        resultat(jade, interne, 6.0);

        afficherRecap();
    }

    // ===================== Helpers =====================

    private Membre creer(String username, String motDePasse, Role role,
                         int niveau, String nom, String prenom, EtatInscription etat) {
        if (membreRepository.existsByUsername(username)) {
            return membreRepository.findByUsername(username).orElseThrow();
        }
        Membre m = new Membre(
                nom, prenom,
                username + "@odoru.fr",
                username,
                passwordEncoder.encode(motDePasse), // mot de passe hache (sinon le login echoue)
                "Toulouse", "France",
                niveau, role, etat,
                LocalDate.now()
        );
        return membreRepository.save(m);
    }

    private void associerBadge(Membre membre, String numero) {
        if (badgeRepository.findByNumeroBadge(numero).isPresent()) return;
        if (badgeRepository.findByMembre(membre).isPresent()) return;
        Badge badge = new Badge(numero);
        badge.setMembre(membre);
        badgeRepository.save(badge);
    }

    private void creerBadgeLibre(String numero) {
        if (badgeRepository.findByNumeroBadge(numero).isPresent()) return;
        badgeRepository.save(new Badge(numero)); // pas de membre associe
    }

    private Cours creerCours(String titre, int niveauCible, DayOfWeek jour, LocalTime heureDebut,
                             String lieu, int dureeMinutes, LocalDate dateDebut, Membre enseignant) {
        for (Cours c : coursRepository.findAll()) {
            if (c.getTitre().equals(titre) && c.getDateDebut().equals(dateDebut)) return c;
        }
        return coursRepository.save(new Cours(titre, niveauCible, jour, heureDebut, lieu, dureeMinutes, dateDebut, enseignant));
    }

    private Competition creerCompetition(String titre, int niveauCible, DayOfWeek jour, LocalTime heureDebut,
                                         String lieu, int dureeMinutes, LocalDate dateDebut, Membre enseignant) {
        for (Competition c : competitionRepository.findAll()) {
            if (c.getTitre().equals(titre) && c.getDateDebut().equals(dateDebut)) return c;
        }
        return competitionRepository.save(new Competition(titre, niveauCible, jour, heureDebut, lieu, dureeMinutes, dateDebut, enseignant));
    }

    /** Enregistre une presence datee au debut reel du cours (date + heure). */
    private void presence(Membre membre, Cours cours, Cours pourDate) {
        if (presenceRepository.existsByMembreAndCours(membre, cours)) return;
        LocalDateTime dateHeure = pourDate.getDateDebut().atTime(pourDate.getHeureDebut()).plusMinutes(5);
        presenceRepository.save(new Presence(membre, cours, dateHeure));
    }

    private void resultat(Membre membre, Competition competition, double note) {
        if (resultatCompetitionRepository.findByMembreAndCompetition(membre, competition).isPresent()) return;
        resultatCompetitionRepository.save(new ResultatCompetition(membre, competition, note));
    }

    private void afficherRecap() {
        System.out.println("\n================= JEU DE DEMO ODORU =================");
        System.out.println("Comptes (username / mot de passe) :");
        System.out.println("  president  / pres123    (PRESIDENT, tous les droits + stats)");
        System.out.println("  secretaire / secret123  (SECRETAIRE : membres, niveaux, inscriptions, badges)");
        System.out.println("  prof       / prof123     (ENSEIGNANT niveau 5 : planifie cours/competitions)");
        System.out.println("  prof.maya  / prof123     (ENSEIGNANT niveau 4 : utile pour 'enseignant non apte')");
        System.out.println("  sami       / eleve123    (ELEVE niveau 3, id=5, badge=BADGE-SAMI-001)");
        System.out.println("  jade       / eleve123    (ELEVE EN_ATTENTE, id=8 : demo activation inscription)");
        System.out.println("  emma       / eleve123    (ELEVE SANS badge, id=14 : demo associer un badge)");
        System.out.println("Scenarios live prets :");
        System.out.println("  - Badge LIVE       : POST /api/badges/badger  { \"numeroBadge\": \"BADGE-SAMI-001\" }");
        System.out.println("  - Cours LIBRE      : VENDREDI 18:00 Studio A (creneau libre, date >= aujourd'hui+7)");
        System.out.println("  - Creneau OCCUPE   : MERCREDI 18:30 Salle CallMe (deja pris -> 409)");
        System.out.println("  - Badge inconnu    : BADGE-XXXX (404)   |   Badge sans membre : BADGE-LIBRE-000 (404)");
        System.out.println("  - Competition niv3 (avec resultats) : id=8   |   Competition a venir : id=9");
        System.out.println("====================================================\n");
    }
}