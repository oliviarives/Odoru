package org.example.odoru.metier;

import org.example.odoru.dao.CoursRepository;
import org.example.odoru.entities.Cours;
import org.example.odoru.entities.Membre;
import org.example.odoru.exceptions.CreneauOccupeException;
import org.example.odoru.exceptions.DateTropProcheException;
import org.example.odoru.exceptions.NiveauInsuffisantException;
import org.example.odoru.export.CoursExport;
import org.example.odoru.export.CoursImport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceCoursTest {

    @Mock
    private CoursRepository coursRepository;

    @Mock
    private ServiceMembre serviceMembre;

    @InjectMocks
    private ServiceCours serviceCours;

    private Membre enseignant(int niveau) {
        Membre m = new Membre();
        m.setId(2L);
        m.setNom("Prof");
        m.setPrenom("Jean");
        m.setNiveauExpertise(niveau);
        return m;
    }

    private CoursImport unCours(int niveauCible, LocalDate date) {
        return new CoursImport("Danse", niveauCible, DayOfWeek.MONDAY,
                LocalTime.of(18, 0), "Salle A", 90, date);
    }

    @Test
    void planifierCours_valide_creeLeCours() {
        when(serviceMembre.recupererMembre(2L)).thenReturn(enseignant(4));
        when(coursRepository.existsByLieuAndJourAndHeureDebut(any(), any(), any())).thenReturn(false);
        when(coursRepository.save(any(Cours.class))).thenAnswer(i -> i.getArgument(0));

        CoursImport coursImport = unCours(3, LocalDate.now().plusDays(10));
        CoursExport export = serviceCours.planifierCours(coursImport, 2L);

        assertEquals("Danse", export.titre());
        assertEquals(3, export.niveauCible());
        verify(coursRepository).save(any(Cours.class));
    }

    @Test
    void planifierCours_dateTropProche_leve_Exception() {
        when(serviceMembre.recupererMembre(2L)).thenReturn(enseignant(4));

        // date à seulement 3 jours -> viole R1 (J+7)
        CoursImport coursImport = unCours(3, LocalDate.now().plusDays(3));

        assertThrows(DateTropProcheException.class,
                () -> serviceCours.planifierCours(coursImport, 2L));
        verify(coursRepository, never()).save(any());
    }

    @Test
    void planifierCours_niveauInsuffisant_leve_Exception() {
        // enseignant niveau 2, cours niveau 4 -> viole R2
        when(serviceMembre.recupererMembre(2L)).thenReturn(enseignant(2));

        CoursImport coursImport = unCours(4, LocalDate.now().plusDays(10));

        assertThrows(NiveauInsuffisantException.class,
                () -> serviceCours.planifierCours(coursImport, 2L));
        verify(coursRepository, never()).save(any());
    }

    @Test
    void planifierCours_creneauOccupe_leve_Exception() {
        when(serviceMembre.recupererMembre(2L)).thenReturn(enseignant(4));
        // le créneau est déjà pris -> viole R3
        when(coursRepository.existsByLieuAndJourAndHeureDebut(any(), any(), any())).thenReturn(true);

        CoursImport coursImport = unCours(3, LocalDate.now().plusDays(10));

        assertThrows(CreneauOccupeException.class,
                () -> serviceCours.planifierCours(coursImport, 2L));
        verify(coursRepository, never()).save(any());
    }
}