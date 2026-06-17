package org.example.odoru.metier;

import org.example.odoru.dao.CompetitionRepository;
import org.example.odoru.dao.ResultatCompetitionRepository;
import org.example.odoru.entities.Competition;
import org.example.odoru.entities.Membre;
import org.example.odoru.entities.ResultatCompetition;
import org.example.odoru.exceptions.DateTropProcheException;
import org.example.odoru.exceptions.NiveauInsuffisantException;
import org.example.odoru.exceptions.NoteInvalideException;
import org.example.odoru.export.CompetitionImport;
import org.example.odoru.export.ResultatExport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceCompetitionTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private ResultatCompetitionRepository resultatRepository;

    @Mock
    private ServiceMembre serviceMembre;

    @InjectMocks
    private ServiceCompetition serviceCompetition;

    private Membre membre(long id, int niveau) {
        Membre m = new Membre();
        m.setId(id);
        m.setNom("Nom");
        m.setPrenom("Prenom");
        m.setNiveauExpertise(niveau);
        return m;
    }

    private Competition competition(long id) {
        Competition c = new Competition();
        c.setId(id);
        c.setTitre("Compet");
        return c;
    }

    private CompetitionImport unImport(int niveauCible, LocalDate date) {
        return new CompetitionImport("Compet", niveauCible, DayOfWeek.SATURDAY,
                LocalTime.of(14, 0), "Gymnase", 120, date);
    }

    // ---------- Planification ----------

    @Test
    void planifier_valide_creeLaCompetition() {
        when(serviceMembre.recupererMembre(2L)).thenReturn(membre(2L, 4));
        when(competitionRepository.save(any(Competition.class))).thenAnswer(i -> i.getArgument(0));

        var export = serviceCompetition.planifierCompetition(unImport(3, LocalDate.now().plusDays(10)), 2L);

        assertEquals("Compet", export.titre());
        verify(competitionRepository).save(any(Competition.class));
    }

    @Test
    void planifier_dateTropProche_leve_Exception() {
        when(serviceMembre.recupererMembre(2L)).thenReturn(membre(2L, 4));

        assertThrows(DateTropProcheException.class,
                () -> serviceCompetition.planifierCompetition(unImport(3, LocalDate.now().plusDays(2)), 2L));
        verify(competitionRepository, never()).save(any());
    }

    @Test
    void planifier_niveauInsuffisant_leve_Exception() {
        when(serviceMembre.recupererMembre(2L)).thenReturn(membre(2L, 2));

        assertThrows(NiveauInsuffisantException.class,
                () -> serviceCompetition.planifierCompetition(unImport(4, LocalDate.now().plusDays(10)), 2L));
        verify(competitionRepository, never()).save(any());
    }

    // ---------- Saisie de note ----------

    @Test
    void indiquerResultat_premiereFois_creeUnNouveauResultat() {
        Membre eleve = membre(1L, 3);
        Competition compet = competition(1L);
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(compet));
        when(serviceMembre.recupererMembre(1L)).thenReturn(eleve);
        when(resultatRepository.findByMembreAndCompetition(eleve, compet)).thenReturn(Optional.empty());
        when(resultatRepository.save(any(ResultatCompetition.class))).thenAnswer(i -> i.getArgument(0));

        ResultatExport export = serviceCompetition.indiquerResultat(1L, 1L, 7.5);

        assertEquals(7.5, export.note());
        verify(resultatRepository).save(any(ResultatCompetition.class));
    }

    @Test
    void indiquerResultat_dejaExistant_metAJourSansDoublon() {
        Membre eleve = membre(1L, 3);
        Competition compet = competition(1L);
        ResultatCompetition existant = new ResultatCompetition(eleve, compet, 5.0);

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(compet));
        when(serviceMembre.recupererMembre(1L)).thenReturn(eleve);
        when(resultatRepository.findByMembreAndCompetition(eleve, compet)).thenReturn(Optional.of(existant));
        when(resultatRepository.save(any(ResultatCompetition.class))).thenAnswer(i -> i.getArgument(0));

        ResultatExport export = serviceCompetition.indiquerResultat(1L, 1L, 8.0);

        // la note du résultat EXISTANT a été mise à jour (pas de nouvelle entité)
        assertEquals(8.0, existant.getNote());
        assertEquals(8.0, export.note());
        verify(resultatRepository).save(existant);
    }

    @Test
    void indiquerResultat_noteHorsBornes_leve_Exception() {
        assertThrows(NoteInvalideException.class,
                () -> serviceCompetition.indiquerResultat(1L, 1L, 12.0));
        assertThrows(NoteInvalideException.class,
                () -> serviceCompetition.indiquerResultat(1L, 1L, -1.0));
    }

    @Test
    void indiquerResultat_precisionInvalide_leve_Exception() {
        // 7.53 a deux décimales -> viole R5 (précision 1/10e)
        assertThrows(NoteInvalideException.class,
                () -> serviceCompetition.indiquerResultat(1L, 1L, 7.53));
    }
}