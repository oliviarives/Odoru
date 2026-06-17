package org.example.odoru.metier;

import org.example.odoru.dao.CompetitionRepository;
import org.example.odoru.dao.CoursRepository;
import org.example.odoru.dao.PresenceRepository;
import org.example.odoru.dao.ResultatCompetitionRepository;
import org.example.odoru.entities.Cours;
import org.example.odoru.entities.Membre;
import org.example.odoru.export.*;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceStats {

    private final CoursRepository coursRepository;
    private final CompetitionRepository competitionRepository;
    private final PresenceRepository presenceRepository;
    private final ResultatCompetitionRepository resultatRepository;
    private final ServiceMembre serviceMembre;

    public ServiceStats(CoursRepository coursRepository,
                        CompetitionRepository competitionRepository,
                        PresenceRepository presenceRepository,
                        ResultatCompetitionRepository resultatRepository,
                        ServiceMembre serviceMembre) {
        this.coursRepository = coursRepository;
        this.competitionRepository = competitionRepository;
        this.presenceRepository = presenceRepository;
        this.resultatRepository = resultatRepository;
        this.serviceMembre = serviceMembre;
    }

    // ---------- Stat A : cours + moyenne de présents ----------

    public StatsGlobalesExport statsCours() {
        List<StatsGlobalesExport.StatCoursExport> details = new ArrayList<>();
        long totalPresents = 0;
        long nombreCours = 0;

        for (Cours cours : coursRepository.findAll()) {
            long presents = presenceRepository.countByCoursId(cours.getId());
            details.add(new StatsGlobalesExport.StatCoursExport(
                    cours.getId(), cours.getTitre(), presents));
            totalPresents += presents;
            nombreCours++;
        }

        double moyenne = nombreCours == 0 ? 0.0 : (double) totalPresents / nombreCours;
        return new StatsGlobalesExport(nombreCours, moyenne, details);
    }

    // ---------- Stat B : cours d'un élève avec présences/absences ----------

    public List<CoursPresenceExport> statsCoursEleve(long eleveId) {
        Membre eleve = serviceMembre.recupererMembre(eleveId);

        // Cours du niveau de l'élève (inscrit de facto)
        List<Cours> coursDuNiveau = coursRepository.findByNiveauCible(eleve.getNiveauExpertise());

        List<CoursPresenceExport> resultat = new ArrayList<>();
        for (Cours cours : coursDuNiveau) {
            boolean present = presenceRepository.existsByMembreAndCours(eleve, cours);
            resultat.add(new CoursPresenceExport(
                    cours.getId(), cours.getTitre(), cours.getDateDebut(), present));
        }
        return resultat;
    }

    // ---------- Stat C : nombre de compétitions pour un niveau ----------

    public StatsCompetitionNiveauExport statsCompetitionsNiveau(int niveauCible) {
        var competitions = competitionRepository.findByNiveauCible(niveauCible);
        List<CompetitionExport> liste = new ArrayList<>();
        competitions.forEach(c -> liste.add(CompetitionExport.depuis(c)));
        return new StatsCompetitionNiveauExport(niveauCible, liste.size(), liste);
    }

    // ---------- Stat D : compétitions d'un élève avec résultats ----------

    public List<ResultatExport> statsCompetitionsEleve(long eleveId) {
        serviceMembre.recupererMembre(eleveId); // valide l'existence (lève MemberNotFound sinon)
        List<ResultatExport> resultat = new ArrayList<>();
        resultatRepository.findByMembreId(eleveId)
                .forEach(r -> resultat.add(ResultatExport.depuis(r)));
        return resultat;
    }
}