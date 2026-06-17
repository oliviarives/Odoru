package org.example.odoru.metier;

import org.example.odoru.dao.CoursRepository;
import org.example.odoru.entities.Cours;
import org.example.odoru.entities.Membre;
import org.example.odoru.exceptions.CreneauOccupeException;
import org.example.odoru.exceptions.DateTropProcheException;
import org.example.odoru.exceptions.NiveauInsuffisantException;
import org.example.odoru.export.CoursExport;
import org.example.odoru.export.CoursImport;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class ServiceCours {

    private final CoursRepository coursRepository;
    private final ServiceMembre serviceMembre;

    public ServiceCours(CoursRepository coursRepository, ServiceMembre serviceMembre) {
        this.coursRepository = coursRepository;
        this.serviceMembre = serviceMembre;
    }

    public CoursExport planifierCours(CoursImport coursImport, long enseignantId) {
        Membre enseignant = serviceMembre.recupererMembre(enseignantId);

        // R1 : la date du cours doit être au moins 7 jours après aujourd'hui
        verifierDelai(coursImport.dateDebut());

        // R2 : l'enseignant doit être apte au niveau du cours
        verifierAptitude(enseignant, coursImport.niveauCible());

        // R3 : le créneau (lieu + jour + heure) doit être libre
        verifierCreneauLibre(coursImport.lieu(), coursImport.jour(), coursImport.heureDebut());

        Cours cours = new Cours(
                coursImport.titre(),
                coursImport.niveauCible(),
                coursImport.jour(),
                coursImport.heureDebut(),
                coursImport.lieu(),
                coursImport.dureeMinutes(),
                coursImport.dateDebut(),
                enseignant
        );
        return CoursExport.depuis(coursRepository.save(cours));
    }

    public CoursExport consulterCours(long id) {
        Cours cours = coursRepository.findById(id)
                .orElseThrow(() -> new org.example.odoru.exceptions.CoursNotFoundException("Aucun cours avec l'id " + id));
        return CoursExport.depuis(cours);
    }

    public List<CoursExport> consulterParNiveau(int niveauCible) {
        return versExport(coursRepository.findByNiveauCible(niveauCible));
    }

    public List<CoursExport> consulterParEnseignant(long enseignantId) {
        return versExport(coursRepository.findByEnseignantId(enseignantId));
    }

    // ----------------- Helpers de validation (règles métier) -----------------

    void verifierDelai(LocalDate dateDebut) {
        LocalDate dateMinimale = LocalDate.now().plusDays(7);
        if (dateDebut.isBefore(dateMinimale)) {
            throw new DateTropProcheException("Un cours doit être planifié au moins 7 jours à l'avance.");
        }
    }

    void verifierAptitude(Membre enseignant, int niveauCible) {
        if (enseignant.getNiveauExpertise() < niveauCible) {
            throw new NiveauInsuffisantException(
                    "L'enseignant (niveau " + enseignant.getNiveauExpertise()
                            + ") n'est pas apte au niveau " + niveauCible + ".");
        }
    }

    void verifierCreneauLibre(String lieu, java.time.DayOfWeek jour, java.time.LocalTime heureDebut) {
        if (coursRepository.existsByLieuAndJourAndHeureDebut(lieu, jour, heureDebut)) {
            throw new CreneauOccupeException(
                    "Le créneau " + jour + " " + heureDebut + " à " + lieu + " est déjà occupé.");
        }
    }

    private List<CoursExport> versExport(Iterable<Cours> cours) {
        List<CoursExport> resultat = new ArrayList<>();
        cours.forEach(c -> resultat.add(CoursExport.depuis(c)));
        return resultat;
    }
}