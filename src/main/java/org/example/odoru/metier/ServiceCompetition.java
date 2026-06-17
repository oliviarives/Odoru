package org.example.odoru.metier;

import org.example.odoru.dao.CompetitionRepository;
import org.example.odoru.dao.ResultatCompetitionRepository;
import org.example.odoru.entities.Competition;
import org.example.odoru.entities.Membre;
import org.example.odoru.entities.ResultatCompetition;
import org.example.odoru.exceptions.CompetitionNotFoundException;
import org.example.odoru.exceptions.DateTropProcheException;
import org.example.odoru.exceptions.NiveauInsuffisantException;
import org.example.odoru.exceptions.NoteInvalideException;
import org.example.odoru.export.CompetitionExport;
import org.example.odoru.export.CompetitionImport;
import org.example.odoru.export.ResultatExport;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceCompetition {

    private final CompetitionRepository competitionRepository;
    private final ResultatCompetitionRepository resultatRepository;
    private final ServiceMembre serviceMembre;

    public ServiceCompetition(CompetitionRepository competitionRepository,
                              ResultatCompetitionRepository resultatRepository,
                              ServiceMembre serviceMembre) {
        this.competitionRepository = competitionRepository;
        this.resultatRepository = resultatRepository;
        this.serviceMembre = serviceMembre;
    }

    // ---------- Planification ----------

    public CompetitionExport planifierCompetition(CompetitionImport import_, long enseignantId) {
        Membre enseignant = serviceMembre.recupererMembre(enseignantId);

        // R1 : date au moins 7 jours à l'avance
        verifierDelai(import_.dateDebut());
        // R2 : enseignant apte au niveau
        verifierAptitude(enseignant, import_.niveauCible());
        // (pas de règle créneau pour les compétitions)

        Competition competition = new Competition(
                import_.titre(),
                import_.niveauCible(),
                import_.jour(),
                import_.heureDebut(),
                import_.lieu(),
                import_.dureeMinutes(),
                import_.dateDebut(),
                enseignant
        );
        return CompetitionExport.depuis(competitionRepository.save(competition));
    }

    public CompetitionExport consulterCompetition(long id) {
        return CompetitionExport.depuis(recupererCompetition(id));
    }

    // ---------- Saisie et consultation des résultats ----------

    public ResultatExport indiquerResultat(long competitionId, long eleveId, double note) {
        // R5 : note sur 10, précision au 1/10e
        verifierNote(note);

        Competition competition = recupererCompetition(competitionId);
        Membre eleve = serviceMembre.recupererMembre(eleveId);

        // INSERT ou UPDATE : si un résultat existe déjà pour ce couple, on le met à jour
        ResultatCompetition resultat = resultatRepository
                .findByMembreAndCompetition(eleve, competition)
                .orElseGet(() -> new ResultatCompetition(eleve, competition, note));
        resultat.setNote(note);

        return ResultatExport.depuis(resultatRepository.save(resultat));
    }

    public List<ResultatExport> consulterResultatsEleve(long eleveId) {
        return versExport(resultatRepository.findByMembreId(eleveId));
    }

    public List<ResultatExport> consulterResultatsCompetition(long competitionId) {
        return versExport(resultatRepository.findByCompetitionId(competitionId));
    }

    // ---------- Helpers ----------

    Competition recupererCompetition(long id) {
        return competitionRepository.findById(id)
                .orElseThrow(() -> new CompetitionNotFoundException("Aucune compétition avec l'id " + id));
    }

    void verifierDelai(LocalDate dateDebut) {
        if (dateDebut.isBefore(LocalDate.now().plusDays(7))) {
            throw new DateTropProcheException("Une compétition doit être planifiée au moins 7 jours à l'avance.");
        }
    }

    void verifierAptitude(Membre enseignant, int niveauCible) {
        if (enseignant.getNiveauExpertise() < niveauCible) {
            throw new NiveauInsuffisantException(
                    "L'enseignant (niveau " + enseignant.getNiveauExpertise()
                            + ") n'est pas apte au niveau " + niveauCible + ".");
        }
    }

    void verifierNote(double note) {
        if (note < 0.0 || note > 10.0) {
            throw new NoteInvalideException("La note doit être comprise entre 0 et 10.");
        }
        // précision au 1/10e : note * 10 doit être un entier
        if (Math.round(note * 10) != note * 10) {
            throw new NoteInvalideException("La note doit avoir une précision au 1/10e (ex: 7.5).");
        }
    }

    private List<ResultatExport> versExport(Iterable<ResultatCompetition> resultats) {
        List<ResultatExport> liste = new ArrayList<>();
        resultats.forEach(r -> liste.add(ResultatExport.depuis(r)));
        return liste;
    }
}