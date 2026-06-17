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
import org.example.odoru.export.PresenceExport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServicePresenceTest {

    @Mock
    private PresenceRepository presenceRepository;

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private CoursRepository coursRepository;

    @InjectMocks
    private ServicePresence servicePresence;

    private Membre eleve(int niveau) {
        Membre m = new Membre();
        m.setId(1L);
        m.setNom("Eleve");
        m.setPrenom("Test");
        m.setNiveauExpertise(niveau);
        return m;
    }

    private Badge badgeAvecMembre(String numero, Membre membre) {
        Badge b = new Badge(numero);
        b.setMembre(membre);
        return b;
    }

    /** Un cours qui a lieu MAINTENANT (aujourd'hui, créneau englobant l'heure courante). */
    private Cours coursCourant(int niveau) {
        Cours c = new Cours();
        c.setId(10L);
        c.setTitre("Cours courant");
        c.setNiveauCible(niveau);
        c.setJour(LocalDate.now().getDayOfWeek());
        // commence il y a 30 min, dure 120 min -> on est dedans
        c.setHeureDebut(LocalTime.now().minusMinutes(30));
        c.setDureeMinutes(120);
        c.setDateDebut(LocalDate.now());
        return c;
    }

    @Test
    void badger_valide_enregistreLaPresence() {
        Membre eleve = eleve(3);
        Cours cours = coursCourant(3);

        when(badgeRepository.findByNumeroBadge("B001"))
                .thenReturn(Optional.of(badgeAvecMembre("B001", eleve)));
        when(coursRepository.findByNiveauCibleAndJour(eq(3), any()))
                .thenReturn(List.of(cours));
        when(presenceRepository.existsByMembreAndCours(eleve, cours)).thenReturn(false);
        when(presenceRepository.save(any(Presence.class))).thenAnswer(i -> i.getArgument(0));

        PresenceExport export = servicePresence.badger("B001");

        assertEquals(1L, export.membreId());
        assertEquals(10L, export.coursId());
        verify(presenceRepository).save(any(Presence.class));
    }

    @Test
    void badger_badgeInconnu_leve_Exception() {
        when(badgeRepository.findByNumeroBadge("XXX")).thenReturn(Optional.empty());

        assertThrows(BadgeNotFoundException.class, () -> servicePresence.badger("XXX"));
        verify(presenceRepository, never()).save(any());
    }

    @Test
    void badger_badgeSansMembre_leve_Exception() {
        Badge badgeSansMembre = new Badge("B001"); // membre == null
        when(badgeRepository.findByNumeroBadge("B001")).thenReturn(Optional.of(badgeSansMembre));

        assertThrows(BadgeNotFoundException.class, () -> servicePresence.badger("B001"));
        verify(presenceRepository, never()).save(any());
    }

    @Test
    void badger_aucunCoursCourant_leve_Exception() {
        Membre eleve = eleve(3);
        when(badgeRepository.findByNumeroBadge("B001"))
                .thenReturn(Optional.of(badgeAvecMembre("B001", eleve)));
        // aucun cours ce jour pour ce niveau
        when(coursRepository.findByNiveauCibleAndJour(eq(3), any())).thenReturn(List.of());

        assertThrows(PasDeCoursCourantException.class, () -> servicePresence.badger("B001"));
        verify(presenceRepository, never()).save(any());
    }

    @Test
    void badger_dejaPresent_leve_Exception() {
        Membre eleve = eleve(3);
        Cours cours = coursCourant(3);

        when(badgeRepository.findByNumeroBadge("B001"))
                .thenReturn(Optional.of(badgeAvecMembre("B001", eleve)));
        when(coursRepository.findByNiveauCibleAndJour(eq(3), any()))
                .thenReturn(List.of(cours));
        // déjà une présence pour ce cours
        when(presenceRepository.existsByMembreAndCours(eleve, cours)).thenReturn(true);

        assertThrows(DejaPresException.class, () -> servicePresence.badger("B001"));
        verify(presenceRepository, never()).save(any());
    }

    @Test
    void consulterHistorique_renvoieLesPresences() {
        Membre eleve = eleve(3);
        Cours cours = coursCourant(3);
        Presence p = new Presence(eleve, cours, java.time.LocalDateTime.now());

        when(presenceRepository.findByMembreId(1L)).thenReturn(List.of(p));

        var historique = servicePresence.consulterHistorique(1L);

        assertEquals(1, historique.size());
        assertEquals(10L, historique.get(0).coursId());
    }
}