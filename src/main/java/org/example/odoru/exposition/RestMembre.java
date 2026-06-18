package org.example.odoru.exposition;

import org.example.odoru.entities.Badge;
import org.example.odoru.export.*;
import org.example.odoru.metier.ServiceBadge;
import org.example.odoru.metier.ServiceCompetition;
import org.example.odoru.metier.ServiceMembre;
import org.example.odoru.metier.ServicePresence;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/membres")
public class RestMembre {

    private final ServiceMembre serviceMembre;
    private final ServiceBadge serviceBadge;
    private final ServiceCompetition serviceCompetition;
    private final ServicePresence servicePresence;

    public RestMembre(ServiceMembre serviceMembre, ServiceBadge serviceBadge,
                      ServiceCompetition serviceCompetition, ServicePresence servicePresence) {
        this.serviceMembre = serviceMembre;
        this.serviceBadge = serviceBadge;
        this.serviceCompetition = serviceCompetition;
        this.servicePresence = servicePresence;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('SECRETAIRE','PRESIDENT')")
    public ResponseEntity<MembreExport> creerMembre(@RequestBody MembreImport membreImport) {
        return new ResponseEntity<>(serviceMembre.creerMembre(membreImport), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('SECRETAIRE','PRESIDENT') or #id == authentication.principal.membre.id")
    public MembreExport consulter(@PathVariable long id) {
        return serviceMembre.consulterMembre(id);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('SECRETAIRE','PRESIDENT')")
    public List<MembreExport> lister() {
        return serviceMembre.listerMembres();
    }

    @PutMapping("/{id}/niveau")
    @PreAuthorize("hasAnyRole('SECRETAIRE','PRESIDENT')")
    public MembreExport modifierNiveau(@PathVariable long id, @RequestBody NiveauImport niveauImport) {
        return serviceMembre.modifierNiveau(id, niveauImport.niveauExpertise());
    }

    @GetMapping("/{id}/inscription")
    @PreAuthorize("hasAnyRole('SECRETAIRE','PRESIDENT')")
    public Map<String, Object> consulterEtat(@PathVariable long id) {
        return Map.of("membreId", id, "etatInscription", serviceMembre.consulterEtatInscription(id));
    }

    @PutMapping("/{id}/inscription")
    @PreAuthorize("hasAnyRole('SECRETAIRE','PRESIDENT')")
    public MembreExport modifierEtat(@PathVariable long id, @RequestBody EtatInscriptionImport etatImport) {
        return serviceMembre.modifierEtatInscription(id, etatImport.etatInscription());
    }

    @PutMapping("/{id}/badge")
    @PreAuthorize("hasAnyRole('SECRETAIRE','PRESIDENT')")
    public Map<String, Object> associerBadge(@PathVariable long id, @RequestBody BadgeImport badgeImport) {
        Badge badge = serviceBadge.associerBadge(id, badgeImport.numeroBadge());
        return Map.of("membreId", id, "numeroBadge", badge.getNumeroBadge());
    }

    @DeleteMapping("/{id}/badge")
    @PreAuthorize("hasAnyRole('SECRETAIRE','PRESIDENT')")
    public Map<String, Object> dissocierBadge(@PathVariable long id) {
        serviceBadge.dissocierBadge(id);
        return Map.of("membreId", id, "message", "Badge dissocié");
    }

    @GetMapping("/{id}/competitions/resultats")
    @PreAuthorize("hasAnyRole('SECRETAIRE','PRESIDENT') or #id == authentication.principal.membre.id")
    public List<ResultatExport> resultatsEleve(@PathVariable long id) {
        return serviceCompetition.consulterResultatsEleve(id);
    }

    @GetMapping("/{id}/historique")
    @PreAuthorize("hasAnyRole('SECRETAIRE','PRESIDENT') or #id == authentication.principal.membre.id")
    public List<HistoriqueExport> historique(@PathVariable long id) {
        return servicePresence.consulterHistorique(id);
    }

    @PutMapping("/{id}/role")
    @PreAuthorize("hasAnyRole('SECRETAIRE','PRESIDENT')")
    public MembreExport modifierRole(@PathVariable long id, @RequestBody RoleImport roleImport) {
        return serviceMembre.modifierRole(id, roleImport.role());
    }

}
