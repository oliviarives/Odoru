package org.example.odoru.metier;

import org.example.odoru.dao.MembreRepository;
import org.example.odoru.entities.EtatInscription;
import org.example.odoru.entities.Membre;
import org.example.odoru.entities.Role;
import org.example.odoru.exceptions.*;
import org.example.odoru.export.MembreExport;
import org.example.odoru.export.MembreImport;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ServiceMembreTest {

    @Mock
    private MembreRepository membreRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ServiceMembre serviceMembre;

    private MembreImport unImport(int niveau) {
        return new MembreImport("Doe", "John", "john@mail.com", "jdoe",
                "secret", "Toulouse", "France", niveau, Role.ELEVE);
    }

    @Test
    void creerMembre_valide_hacheLePassword_etSauvegarde() {
        when(membreRepository.existsByUsername("jdoe")).thenReturn(false);
        when(membreRepository.existsByEmail("john@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("HASH");
        when(membreRepository.save(any(Membre.class))).thenAnswer(i -> i.getArgument(0));

        MembreExport export = serviceMembre.creerMembre(unImport(3));

        assertEquals("jdoe", export.username());
        assertEquals(EtatInscription.ACTIVE, export.etatInscription());
        verify(passwordEncoder).encode("secret");
        verify(membreRepository).save(argThat(m -> m.getPassword().equals("HASH")));
    }

    @Test
    void creerMembre_usernamePris_leve_Exception() {
        when(membreRepository.existsByUsername("jdoe")).thenReturn(true);

        assertThrows(UsernameAlreadyExistException.class, () -> serviceMembre.creerMembre(unImport(3)));
        verify(membreRepository, never()).save(any());
    }

    @Test
    void creerMembre_emailPris_leve_Exception() {
        when(membreRepository.existsByUsername("jdoe")).thenReturn(false);
        when(membreRepository.existsByEmail("john@mail.com")).thenReturn(true);

        assertThrows(EmailAlreadyExistException.class, () -> serviceMembre.creerMembre(unImport(3)));
        verify(membreRepository, never()).save(any());
    }

    @Test
    void creerMembre_niveauHorsBornes_leve_Exception() {
        when(membreRepository.existsByUsername(any())).thenReturn(false);
        when(membreRepository.existsByEmail(any())).thenReturn(false);

        assertThrows(NiveauInvalideException.class, () -> serviceMembre.creerMembre(unImport(6)));
        assertThrows(NiveauInvalideException.class, () -> serviceMembre.creerMembre(unImport(0)));
    }

    @Test
    void recupererMembre_inexistant_leve_Exception() {
        when(membreRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(MemberNotFoundException.class, () -> serviceMembre.recupererMembre(99L));
    }

    @Test
    void modifierNiveau_valide_metAJour() {
        Membre m = new Membre();
        m.setId(1L);
        m.setNiveauExpertise(2);
        when(membreRepository.findById(1L)).thenReturn(Optional.of(m));
        when(membreRepository.save(any(Membre.class))).thenAnswer(i -> i.getArgument(0));

        MembreExport export = serviceMembre.modifierNiveau(1L, 4);

        assertEquals(4, export.niveauExpertise());
    }

    @Test
    void modifierNiveau_invalide_leve_Exception() {
        assertThrows(NiveauInvalideException.class, () -> serviceMembre.modifierNiveau(1L, 7));
        verify(membreRepository, never()).save(any());
    }

    @Test
    void modifierRole_valide_metAJour() {
        Membre m = new Membre();
        m.setId(1L);
        m.setRole(Role.ELEVE);
        when(membreRepository.findById(1L)).thenReturn(Optional.of(m));
        when(membreRepository.save(any(Membre.class))).thenAnswer(i -> i.getArgument(0));

        MembreExport export = serviceMembre.modifierRole(1L, Role.ENSEIGNANT);

        assertEquals(Role.ENSEIGNANT, export.role());
    }

    @Test
    void modifierRole_versPresident_leve_Exception() {
        assertThrows(RoleInvalideException.class,
                () -> serviceMembre.modifierRole(1L, Role.PRESIDENT));
        verify(membreRepository, never()).save(any());
    }
}