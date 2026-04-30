package exceptions;

public class BadgeAlreadyAssociatedException extends RuntimeException {
    public BadgeAlreadyAssociatedException(String message) {
        super(message);
    }
}
