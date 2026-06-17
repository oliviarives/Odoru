package org.example.odoru.secu;

import org.example.odoru.dao.MembreRepository;
import org.example.odoru.entities.Membre;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final MembreRepository membreRepository;

    public UserDetailsServiceImpl(MembreRepository membreRepository) {
        this.membreRepository = membreRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Membre membre = membreRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Membre introuvable : " + username));
        return new MembreDetails(membre);
    }
}