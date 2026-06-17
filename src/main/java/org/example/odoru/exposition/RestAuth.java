package org.example.odoru.exposition;

import org.example.odoru.export.InscriptionImport;
import org.example.odoru.export.LoginImport;
import org.example.odoru.export.MembreExport;
import org.example.odoru.export.TokenExport;
import org.example.odoru.metier.ServiceAuth;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class RestAuth {

    private final ServiceAuth serviceAuth;

    public RestAuth(ServiceAuth serviceAuth) {
        this.serviceAuth = serviceAuth;
    }

    @PostMapping("/register")
    public ResponseEntity<MembreExport> inscrire(@RequestBody InscriptionImport inscription) {
        MembreExport membre = serviceAuth.inscrire(inscription);
        return new ResponseEntity<>(membre, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public TokenExport login(@RequestBody LoginImport loginImport) {
        return serviceAuth.login(loginImport.username(), loginImport.password());
    }
}