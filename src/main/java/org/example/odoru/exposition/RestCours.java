package org.example.odoru.exposition;

import org.example.odoru.export.CoursExport;
import org.example.odoru.export.CoursImport;
import org.example.odoru.metier.ServiceCours;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cours")
public class RestCours {

    private final ServiceCours serviceCours;

    public RestCours(ServiceCours serviceCours) {
        this.serviceCours = serviceCours;
    }

    @PostMapping
    public ResponseEntity<CoursExport> planifier(
            @RequestBody CoursImport coursImport,
            @RequestParam long enseignantId) {
        CoursExport cours = serviceCours.planifierCours(coursImport, enseignantId);
        return new ResponseEntity<>(cours, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public CoursExport consulter(@PathVariable long id) {
        return serviceCours.consulterCours(id);
    }

    @GetMapping
    public List<CoursExport> consulter(
            @RequestParam(required = false) Integer niveauCible,
            @RequestParam(required = false) Long enseignantId) {
        if (niveauCible != null) {
            return serviceCours.consulterParNiveau(niveauCible);
        }
        if (enseignantId != null) {
            return serviceCours.consulterParEnseignant(enseignantId);
        }
        return List.of(); // ni niveau ni enseignant fourni : liste vide
    }
}