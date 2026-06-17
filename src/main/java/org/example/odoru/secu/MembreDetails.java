package org.example.odoru.secu;

import org.example.odoru.entities.Membre;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class MembreDetails implements UserDetails {

    private final Membre membre;

    public MembreDetails(Membre membre) {
        this.membre = membre;
    }

    public Membre getMembre() {
        return membre;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Spring attend des rôles préfixés par "ROLE_"
        return List.of(new SimpleGrantedAuthority("ROLE_" + membre.getRole().name()));
    }

    @Override
    public String getPassword() {
        return membre.getPassword();
    }

    @Override
    public String getUsername() {
        return membre.getUsername();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}