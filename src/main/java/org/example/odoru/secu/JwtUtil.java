package org.example.odoru.secu;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.example.odoru.entities.Membre;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {

    @Value("${odoru.jwt.secret}")
    private String secret;

    @Value("${odoru.jwt.expiration}")
    private long expiration;

    private SecretKey getCle() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** Génère un token pour un membre (username + rôle dans les claims). */
    public String genererToken(Membre membre) {
        Date maintenant = new Date();
        Date expirationDate = new Date(maintenant.getTime() + expiration);

        return Jwts.builder()
                .subject(membre.getUsername())
                .claim("role", membre.getRole().name())
                .claim("membreId", membre.getId())
                .issuedAt(maintenant)
                .expiration(expirationDate)
                .signWith(getCle())
                .compact();
    }

    /** Extrait le username (subject) du token. */
    public String extraireUsername(String token) {
        return extraireClaims(token).getSubject();
    }

    /** Vérifie que le token est valide (signature + non expiré). */
    public boolean estValide(String token) {
        try {
            extraireClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Claims extraireClaims(String token) {
        return Jwts.parser()
                .verifyWith(getCle())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}