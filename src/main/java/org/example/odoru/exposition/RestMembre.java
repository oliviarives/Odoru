package org.example.odoru.exposition;

import org.example.odoru.entities.Badge;
import org.example.odoru.export.*;
import org.example.odoru.metier.ServiceBadge;
import org.example.odoru.metier.ServiceCompetition;
import org.example.odoru.metier.ServiceMembre;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/membres")
public class RestMembre {

    private final ServiceMembre serviceMembre;
    private final ServiceBadge serviceBadge;
    private final ServiceCompetition serviceCompetition;

    public RestMembre(ServiceMembre serviceMembre, ServiceBadge serviceBadge,
                      ServiceCompetition serviceCompetition) {
        this.serviceMembre = serviceMembre;
        this.serviceBadge = serviceBadge;
        this.serviceCompetition = serviceCompetition;
    }

    @PostMapping
    public ResponseEntity<MembreExport> creerMembre(@RequestBody MembreImport membreImport) {
        return new ResponseEntity<>(serviceMembre.creerMembre(membreImport), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public MembreExport consulter(@PathVariable long id) {
        return serviceMembre.consulterMembre(id);
    }

    @GetMapping
    public List<MembreExport> lister() {
        return serviceMembre.listerMembres();
    }

    @PutMapping("/{id}/niveau")
    public MembreExport modifierNiveau(@PathVariable long id, @RequestBody NiveauImport niveauImport) {
        return serviceMembre.modifierNiveau(id, niveauImport.niveauExpertise());
    }

    @GetMapping("/{id}/inscription")
    public Map<String, Object> consulterEtat(@PathVariable long id) {
        return Map.of("membreId", id, "etatInscription", serviceMembre.consulterEtatInscription(id));
    }

    @PutMapping("/{id}/inscription")
    public MembreExport modifierEtat(@PathVariable long id, @RequestBody EtatInscriptionImport etatImport) {
        return serviceMembre.modifierEtatInscription(id, etatImport.etatInscription());
    }

    @PutMapping("/{id}/badge")
    public Map<String, Object> associerBadge(@PathVariable long id, @RequestBody BadgeImport badgeImport) {
        Badge badge = serviceBadge.associerBadge(id, badgeImport.numeroBadge());
        return Map.of("membreId", id, "numeroBadge", badge.getNumeroBadge());
    }

    @DeleteMapping("/{id}/badge")
    public Map<String, Object> dissocierBadge(@PathVariable long id) {
        serviceBadge.dissocierBadge(id);
        return Map.of("membreId", id, "message", "Badge dissocié");
    }

    @GetMapping("/{id}/competitions/resultats")
    public List<ResultatExport> resultatsEleve(@PathVariable long id) {
        return serviceCompetition.consulterResultatsEleve(id);
    }
}
