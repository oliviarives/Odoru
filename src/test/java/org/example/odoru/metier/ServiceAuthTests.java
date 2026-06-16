package org.example.odoru.metier;

import org.example.odoru.dao.MembreRepository;
import org.example.odoru.entities.EtatInscription;
import org.example.odoru.entities.Membre;
import org.example.odoru.entities.Role;
import org.example.odoru.exceptions.EmailAlreadyExistException;
import org.example.odoru.exceptions.NiveauInvalideException;
import org.example.odoru.exceptions.UsernameAlreadyExistException;
import org.example.odoru.export.InscriptionImport;
import org.example.odoru.export.MembreExport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceAuthTest {

    @Mock
    private MembreRepository membreRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ServiceAuth serviceAuth;

    private InscriptionImport uneInscription(int niveau) {
        return new InscriptionImport("Doe", "Jane", "jane@mail.com", "jane",
                "secret", "Lyon", "France", niveau);
    }

    @Test
    void inscrire_valide_roleEleve_etatEnAttente_passwordHashe() {
        when(membreRepository.existsByUsername("jane")).thenReturn(false);
        when(membreRepository.existsByEmail("jane@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("HASH");
        when(membreRepository.save(any(Membre.class))).thenAnswer(i -> i.getArgument(0));

        MembreExport export = serviceAuth.inscrire(uneInscription(2));

        assertEquals(Role.ELEVE, export.role());
        assertEquals(EtatInscription.EN_ATTENTE, export.etatInscription());
        verify(passwordEncoder).encode("secret");
        verify(membreRepository).save(argThat(m ->
                m.getRole() == Role.ELEVE
                        && m.getEtatInscription() == EtatInscription.EN_ATTENTE
                        && m.getPassword().equals("HASH")));
    }

    @Test
    void inscrire_usernamePris_leve_Exception() {
        when(membreRepository.existsByUsername("jane")).thenReturn(true);

        assertThrows(UsernameAlreadyExistException.class, () -> serviceAuth.inscrire(uneInscription(2)));
        verify(membreRepository, never()).save(any());
    }

    @Test
    void inscrire_emailPris_leve_Exception() {
        when(membreRepository.existsByUsername("jane")).thenReturn(false);
        when(membreRepository.existsByEmail("jane@mail.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistException.class, () -> serviceAuth.inscrire(uneInscription(2)));
        verify(membreRepository, never()).save(any());
    }

    @Test
    void inscrire_niveauInvalide_leve_Exception() {
        assertThrows(NiveauInvalideException.class, () -> serviceAuth.inscrire(uneInscription(0)));
        assertThrows(NiveauInvalideException.class, () -> serviceAuth.inscrire(uneInscription(6)));
        verify(membreRepository, never()).save(any());
    }
}