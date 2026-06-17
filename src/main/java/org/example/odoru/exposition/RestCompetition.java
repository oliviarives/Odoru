package org.example.odoru.exposition;

import org.example.odoru.export.CompetitionExport;
import org.example.odoru.export.CompetitionImport;
import org.example.odoru.export.NoteImport;
import org.example.odoru.export.ResultatExport;
import org.example.odoru.metier.ServiceCompetition;
import org.example.odoru.secu.MembreDetails;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
    @PreAuthorize("hasRole('ENSEIGNANT')")
    public ResponseEntity<CompetitionExport> planifier(
            @RequestBody CompetitionImport competitionImport,
            @AuthenticationPrincipal MembreDetails enseignant) {
        CompetitionExport c = serviceCompetition.planifierCompetition(
                competitionImport, enseignant.getMembre().getId());
        return new ResponseEntity<>(c, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public CompetitionExport consulter(@PathVariable long id) {
        return serviceCompetition.consulterCompetition(id);
    }

    // ---------- Résultats ----------

    @PutMapping("/{id}/resultats/{eleveId}")
    @PreAuthorize("hasAnyRole('ENSEIGNANT','PRESIDENT')")
    public ResultatExport indiquerResultat(
            @PathVariable long id,
            @PathVariable long eleveId,
            @RequestBody NoteImport noteImport) {
        return serviceCompetition.indiquerResultat(id, eleveId, noteImport.note());
    }

    @GetMapping("/{id}/resultats")
    @PreAuthorize("hasRole('ENSEIGNANT','PRESIDENT')")
    public List<ResultatExport> resultatsCompetition(@PathVariable long id) {
        return serviceCompetition.consulterResultatsCompetition(id);
    }
}