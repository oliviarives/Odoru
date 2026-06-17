package org.example.odoru.metier;

import org.example.odoru.dao.BadgeRepository;
import org.example.odoru.dao.CoursRepository;
import org.example.odoru.dao.PresenceRepository;
import org.example.odoru.entities.Badge;
import org.example.odoru.entities.Cours;
import org.example.odoru.entities.Membre;
import org.example.odoru.entities.Presence;
import org.example.odoru.exceptions.BadgeNotFoundException;
import org.example.odoru.exceptions.DejaPresException;
import org.example.odoru.exceptions.PasDeCoursCourantException;
import org.example.odoru.export.HistoriqueExport;
import org.example.odoru.export.PresenceExport;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServicePresence {

    private final PresenceRepository presenceRepository;
    private final BadgeRepository badgeRepository;
    private final CoursRepository coursRepository;

    public ServicePresence(PresenceRepository presenceRepository,
                           BadgeRepository badgeRepository,
                           CoursRepository coursRepository) {
        this.presenceRepository = presenceRepository;
        this.badgeRepository = badgeRepository;
        this.coursRepository = coursRepository;
    }

    /**
     * Le boîtier appelle cette méthode quand un élève badge.
     * On identifie l'élève via le badge, puis le cours courant via son niveau + le moment.
     */
    public PresenceExport badger(String numeroBadge) {
        // 1. Identifier le porteur du badge
        Badge badge = badgeRepository.findByNumeroBadge(numeroBadge)
                .orElseThrow(() -> new BadgeNotFoundException("Badge inconnu : " + numeroBadge));

        Membre eleve = badge.getMembre();
        if (eleve == null) {
            throw new BadgeNotFoundException("Le badge " + numeroBadge + " n'est associé à aucun membre.");
        }

        // 2. Trouver le cours courant pour le niveau de l'élève, aujourd'hui
        Cours coursCourant = trouverCoursCourant(eleve);

        // 3. Vérifier qu'il n'a pas déjà badgé pour ce cours
        if (presenceRepository.existsByMembreAndCours(eleve, coursCourant)) {
            throw new DejaPresException("Présence déjà enregistrée pour ce cours.");
        }

        // 4. Enregistrer la présence
        Presence presence = new Presence(eleve, coursCourant, LocalDateTime.now());
        return PresenceExport.depuis(presenceRepository.save(presence));
    }

    /**
     * Historique des cours suivis par un élève.
     */
    public List<HistoriqueExport> consulterHistorique(long eleveId) {
        List<HistoriqueExport> historique = new ArrayList<>();
        presenceRepository.findByMembreId(eleveId)
                .forEach(p -> historique.add(HistoriqueExport.depuis(p)));
        return historique;
    }

    // ----------------- Helper -----------------

    private Cours trouverCoursCourant(Membre eleve) {
        LocalDate aujourdhui = LocalDate.now();
        LocalTime maintenant = LocalTime.now();

        // Cours du niveau de l'élève, le jour de la semaine actuel
        List<Cours> candidats = coursRepository.findByNiveauCibleAndJour(
                eleve.getNiveauExpertise(), aujourdhui.getDayOfWeek());

        // Parmi eux, celui dont le créneau horaire contient l'instant présent
        for (Cours cours : candidats) {
            LocalTime debut = cours.getHeureDebut();
            LocalTime fin = debut.plusMinutes(cours.getDureeMinutes());
            if (!maintenant.isBefore(debut) && maintenant.isBefore(fin)) {
                return cours;
            }
        }
        throw new PasDeCoursCourantException(
                "Aucun cours en cours pour le niveau " + eleve.getNiveauExpertise() + " actuellement.");
    }
}