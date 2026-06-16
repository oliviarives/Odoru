package org.example.odoru.metier;

import org.example.odoru.dao.BadgeRepository;
import org.example.odoru.entities.Badge;
import org.example.odoru.entities.Membre;
import org.example.odoru.exceptions.BadgeAlreadyAssociatedException;
import org.example.odoru.exceptions.BadgeNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ServiceBadge {

    private final BadgeRepository badgeRepository;
    private final ServiceMembre serviceMembre;

    public ServiceBadge(BadgeRepository badgeRepository, ServiceMembre serviceMembre) {
        this.badgeRepository = badgeRepository;
        this.serviceMembre = serviceMembre;
    }

    public Badge associerBadge(long membreId, String numeroBadge) {
        Membre membre = serviceMembre.recupererMembre(membreId);

        Badge badge = badgeRepository.findByNumeroBadge(numeroBadge)
                .orElseGet(() -> new Badge(numeroBadge));

        if (badge.getMembre() != null && !badge.getMembre().getId().equals(membreId)) {
            throw new BadgeAlreadyAssociatedException("Le badge " + numeroBadge + " est déjà attribué à un autre membre.");
        }

        badge.setMembre(membre);
        return badgeRepository.save(badge);
    }

    public void dissocierBadge(long membreId) {
        Membre membre = serviceMembre.recupererMembre(membreId);

        Badge badge = badgeRepository.findByMembre(membre)
                .orElseThrow(() -> new BadgeNotFoundException("Aucun badge associé au membre " + membreId));

        badge.setMembre(null);
        badgeRepository.save(badge);
    }
}
