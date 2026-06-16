package org.example.odoru.metier;

import org.example.odoru.dao.BadgeRepository;
import org.example.odoru.entities.Badge;
import org.example.odoru.entities.Membre;
import org.example.odoru.exceptions.BadgeAlreadyAssociatedException;
import org.example.odoru.exceptions.BadgeNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceBadgeTest {

    @Mock
    private BadgeRepository badgeRepository;

    @Mock
    private ServiceMembre serviceMembre;

    @InjectMocks
    private ServiceBadge serviceBadge;

    private Membre membre(long id) {
        Membre m = new Membre();
        m.setId(id);
        return m;
    }

    @Test
    void associerBadge_badgeLibre_associeAuMembre() {
        Membre m = membre(1L);
        when(serviceMembre.recupererMembre(1L)).thenReturn(m);
        when(badgeRepository.findByNumeroBadge("ABC")).thenReturn(Optional.empty());
        when(badgeRepository.save(any(Badge.class))).thenAnswer(i -> i.getArgument(0));

        Badge badge = serviceBadge.associerBadge(1L, "ABC");

        assertEquals("ABC", badge.getNumeroBadge());
        assertEquals(m, badge.getMembre());
    }

    @Test
    void associerBadge_dejaAttribueAutreMembre_leve_Exception() {
        Membre m1 = membre(1L);
        Membre m2 = membre(2L);
        Badge badgeExistant = new Badge("ABC");
        badgeExistant.setMembre(m2);

        when(serviceMembre.recupererMembre(1L)).thenReturn(m1);
        when(badgeRepository.findByNumeroBadge("ABC")).thenReturn(Optional.of(badgeExistant));

        assertThrows(BadgeAlreadyAssociatedException.class, () -> serviceBadge.associerBadge(1L, "ABC"));
        verify(badgeRepository, never()).save(any());
    }

    @Test
    void dissocierBadge_existant_metMembreANull_sansSupprimer() {
        Membre m = membre(1L);
        Badge badge = new Badge("ABC");
        badge.setMembre(m);

        when(serviceMembre.recupererMembre(1L)).thenReturn(m);
        when(badgeRepository.findByMembre(m)).thenReturn(Optional.of(badge));
        when(badgeRepository.save(any(Badge.class))).thenAnswer(i -> i.getArgument(0));

        serviceBadge.dissocierBadge(1L);

        assertNull(badge.getMembre());
        verify(badgeRepository).save(badge);
        verify(badgeRepository, never()).delete(any());
    }

    @Test
    void dissocierBadge_aucunBadge_leve_Exception() {
        Membre m = membre(1L);
        when(serviceMembre.recupererMembre(1L)).thenReturn(m);
        when(badgeRepository.findByMembre(m)).thenReturn(Optional.empty());

        assertThrows(BadgeNotFoundException.class, () -> serviceBadge.dissocierBadge(1L));
    }
}