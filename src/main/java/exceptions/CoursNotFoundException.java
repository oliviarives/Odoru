package exceptions;

public class CoursNotFoundException extends RuntimeException {
    public CoursNotFoundException(String message) {
        super(message);
    }
}
