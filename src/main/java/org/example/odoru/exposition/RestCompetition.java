package org.example.odoru.exposition;

import org.example.odoru.export.CompetitionExport;
import org.example.odoru.export.CompetitionImport;
import org.example.odoru.export.NoteImport;
import org.example.odoru.export.ResultatExport;
import org.example.odoru.metier.ServiceCompetition;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/competitions")
public class RestCompetition {

    private final ServiceCompetition serviceCompetition;

    public RestCompetition(ServiceCompetition serviceCompetition) {
        this.serviceCompetition = serviceCompetition;
    }

    // ---------- Planification ----------

    @PostMapping
    public ResponseEntity<CompetitionExport> planifier(
            @RequestBody CompetitionImport competitionImport,
            @RequestParam long enseignantId) {
        CompetitionExport competition = serviceCompetition.planifierCompetition(competitionImport, enseignantId);
        return new ResponseEntity<>(competition, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public CompetitionExport consulter(@PathVariable long id) {
        return serviceCompetition.consulterCompetition(id);
    }

    // ---------- Résultats ----------

    @PutMapping("/{id}/resultats/{eleveId}")
    public ResultatExport indiquerResultat(
            @PathVariable long id,
            @PathVariable long eleveId,
            @RequestBody NoteImport noteImport) {
        return serviceCompetition.indiquerResultat(id, eleveId, noteImport.note());
    }

    @GetMapping("/{id}/resultats")
    public List<ResultatExport> resultatsCompetition(@PathVariable long id) {
        return serviceCompetition.consulterResultatsCompetition(id);
    }
}