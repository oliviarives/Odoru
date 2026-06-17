package org.example.odoru.exposition;

import org.example.odoru.export.CoursPresenceExport;
import org.example.odoru.export.ResultatExport;
import org.example.odoru.export.StatsCompetitionNiveauExport;
import org.example.odoru.export.StatsGlobalesExport;
import org.example.odoru.metier.ServiceStats;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stats")
@PreAuthorize("hasRole('PRESIDENT')")
public class RestStats {

    private final ServiceStats serviceStats;

    public RestStats(ServiceStats serviceStats) {
        this.serviceStats = serviceStats;
    }

    // Stat A : cours + moyenne de présents
    @GetMapping("/cours")
    public StatsGlobalesExport statsCours() {
        return serviceStats.statsCours();
    }

    // Stat B : cours d'un élève avec présences/absences
    @GetMapping("/membres/{id}/cours")
    public List<CoursPresenceExport> statsCoursEleve(@PathVariable long id) {
        return serviceStats.statsCoursEleve(id);
    }

    // Stat C : nombre de compétitions pour un niveau
    @GetMapping("/competitions")
    public StatsCompetitionNiveauExport statsCompetitionsNiveau(@RequestParam int niveauCible) {
        return serviceStats.statsCompetitionsNiveau(niveauCible);
    }

    // Stat D : compétitions d'un élève avec résultats
    @GetMapping("/membres/{id}/competitions")
    public List<ResultatExport> statsCompetitionsEleve(@PathVariable long id) {
        return serviceStats.statsCompetitionsEleve(id);
    }
}