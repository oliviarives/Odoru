package org.example.odoru.exposition;

import org.example.odoru.export.BadgerImport;
import org.example.odoru.export.PresenceExport;
import org.example.odoru.metier.ServicePresence;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/badges")
public class RestBadge {

    private final ServicePresence servicePresence;

    public RestBadge(ServicePresence servicePresence) {
        this.servicePresence = servicePresence;
    }

    /**
     * Endpoint appelé par le boîtier physique quand un élève badge.
     * Pas de token : c'est un appel machine-to-machine (simulé).
     */
    @PostMapping("/badger")
    public PresenceExport badger(@RequestBody BadgerImport badgerImport) {
        return servicePresence.badger(badgerImport.numeroBadge());
    }
}