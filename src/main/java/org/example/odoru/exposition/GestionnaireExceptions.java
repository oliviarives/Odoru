package org.example.odoru.exposition;

import org.example.odoru.exceptions.*;
import org.example.odoru.export.ErrorExport;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GestionnaireExceptions {

    // ---------- 404 NOT FOUND ----------

    @ExceptionHandler(MemberNotFoundException.class)
    public ResponseEntity<ErrorExport> gereMembreInexistant(MemberNotFoundException e) {
        return reponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadgeNotFoundException.class)
    public ResponseEntity<ErrorExport> gereBadgeInexistant(BadgeNotFoundException e) {
        return reponse(e, HttpStatus.NOT_FOUND);
    }

    // ---------- 400 BAD REQUEST ----------

    @ExceptionHandler(NiveauInvalideException.class)
    public ResponseEntity<ErrorExport> gereNiveauInvalide(NiveauInvalideException e) {
        return reponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EtatInvalideException.class)
    public ResponseEntity<ErrorExport> gereEtatInvalide(EtatInvalideException e) {
        return reponse(e, HttpStatus.BAD_REQUEST);
    }

    // ---------- 403 FORBIDDEN ----------

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorExport> gereAccessDenied(AccessDeniedException e) {
        return reponse(e, HttpStatus.FORBIDDEN);
    }

    // ---------- 409 CONFLICT ----------

    @ExceptionHandler(EmailAlreadyExistException.class)
    public ResponseEntity<ErrorExport> gereEmailDejaUtilise(EmailAlreadyExistException e) {
        return reponse(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(UsernameAlreadyExistException.class)
    public ResponseEntity<ErrorExport> gereUsernameDejaUtilise(UsernameAlreadyExistException e) {
        return reponse(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadgeAlreadyAssociatedException.class)
    public ResponseEntity<ErrorExport> gereBadgeDejaAttribue(BadgeAlreadyAssociatedException e) {
        return reponse(e, HttpStatus.CONFLICT);
    }

    // ---------- 500 (filet de sécurité) ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorExport> gereAutreException(Exception e) {
        e.printStackTrace();
        return reponse(e, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private ResponseEntity<ErrorExport> reponse(Exception e, HttpStatus status) {
        return new ResponseEntity<>(
                new ErrorExport(e.getMessage(), e.getClass().getSimpleName()),
                status
        );
    }

    // ---------- Mouvement II : cours ----------

    @ExceptionHandler(CoursNotFoundException.class)
    public ResponseEntity<ErrorExport> gereCoursInexistant(CoursNotFoundException e) {
        return reponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DateTropProcheException.class)
    public ResponseEntity<ErrorExport> gereDateTropProche(DateTropProcheException e) {
        return reponse(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NiveauInsuffisantException.class)
    public ResponseEntity<ErrorExport> gereNiveauInsuffisant(NiveauInsuffisantException e) {
        return reponse(e, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CreneauOccupeException.class)
    public ResponseEntity<ErrorExport> gereCreneauOccupe(CreneauOccupeException e) {
        return reponse(e, HttpStatus.CONFLICT);
    }

    // ---------- Mouvement III : compétitions ----------

    @ExceptionHandler(CompetitionNotFoundException.class)
    public ResponseEntity<ErrorExport> gereCompetitionInexistante(CompetitionNotFoundException e) {
        return reponse(e, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoteInvalideException.class)
    public ResponseEntity<ErrorExport> gereNoteInvalide(NoteInvalideException e) {
        return reponse(e, HttpStatus.BAD_REQUEST);
    }

    // ---------- Mouvement IV : badges / présences ----------

    @ExceptionHandler(PasDeCoursCourantException.class)
    public ResponseEntity<ErrorExport> gerePasDeCoursCourant(PasDeCoursCourantException e) {
        return reponse(e, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(DejaPresException.class)
    public ResponseEntity<ErrorExport> gereDejaPresent(DejaPresException e) {
        return reponse(e, HttpStatus.CONFLICT);
    }
}
