package org.example.odoru.metier;

import org.example.odoru.dao.CompetitionRepository;
import org.example.odoru.dao.CoursRepository;
import org.example.odoru.dao.PresenceRepository;
import org.example.odoru.dao.ResultatCompetitionRepository;
import org.example.odoru.entities.Cours;
import org.example.odoru.entities.Membre;
import org.example.odoru.export.CoursPresenceExport;
import org.example.odoru.export.StatsGlobalesExport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceStatsTest {

    @Mock
    private CoursRepository coursRepository;

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private PresenceRepository presenceRepository;

    @Mock
    private ResultatCompetitionRepository resultatRepository;

    @Mock
    private ServiceMembre serviceMembre;

    @InjectMocks
    private ServiceStats serviceStats;

    private Cours cours(long id, int niveau) {
        Cours c = new Cours();
        c.setId(id);
        c.setTitre("Cours " + id);
        c.setNiveauCible(niveau);
        c.setDateDebut(LocalDate.now());
        return c;
    }

    private Membre eleve(int niveau) {
        Membre m = new Membre();
        m.setId(1L);
        m.setNiveauExpertise(niveau);
        return m;
    }

    // ---------- Stat A : moyenne de présents ----------

    @Test
    void statsCours_calculeLaMoyenne() {
        Cours c1 = cours(1L, 3);
        Cours c2 = cours(2L, 3);
        when(coursRepository.findAll()).thenReturn(List.of(c1, c2));
        when(presenceRepository.countByCoursId(1L)).thenReturn(4L); // 4 présents
        when(presenceRepository.countByCoursId(2L)).thenReturn(2L); // 2 présents

        StatsGlobalesExport stats = serviceStats.statsCours();

        assertEquals(2, stats.nombreCours());
        assertEquals(3.0, stats.moyenneElevesPresents()); // (4 + 2) / 2 = 3.0
        assertEquals(2, stats.details().size());
    }

    @Test
    void statsCours_aucunCours_moyenneZero_sansDivisionParZero() {
        when(coursRepository.findAll()).thenReturn(List.of());

        StatsGlobalesExport stats = serviceStats.statsCours();

        assertEquals(0, stats.nombreCours());
        assertEquals(0.0, stats.moyenneElevesPresents()); // pas de division par zéro
    }

    // ---------- Stat B : présences ET absences ----------

    @Test
    void statsCoursEleve_indiquePresencesEtAbsences() {
        Membre eleve = eleve(3);
        Cours c1 = cours(1L, 3);
        Cours c2 = cours(2L, 3);

        when(serviceMembre.recupererMembre(1L)).thenReturn(eleve);
        when(coursRepository.findByNiveauCible(3)).thenReturn(List.of(c1, c2));
        when(presenceRepository.existsByMembreAndCours(eleve, c1)).thenReturn(true);  // présent
        when(presenceRepository.existsByMembreAndCours(eleve, c2)).thenReturn(false); // absent

        List<CoursPresenceExport> resultat = serviceStats.statsCoursEleve(1L);

        assertEquals(2, resultat.size());
        assertTrue(resultat.get(0).present());   // c1 : présent
        assertFalse(resultat.get(1).present());  // c2 : absent
    }

    // ---------- Stat C : compétitions par niveau ----------

    @Test
    void statsCompetitionsNiveau_compteLesCompetitions() {
        when(competitionRepository.findByNiveauCible(3)).thenReturn(List.of(
                new org.example.odoru.entities.Competition(),
                new org.example.odoru.entities.Competition()
        ));

        var stats = serviceStats.statsCompetitionsNiveau(3);

        assertEquals(3, stats.niveauCible());
        assertEquals(2, stats.nombreCompetitions());
    }
}