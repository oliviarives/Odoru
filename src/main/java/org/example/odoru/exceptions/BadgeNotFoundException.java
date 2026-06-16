package org.example.odoru.exceptions;

public class BadgeNotFoundException extends RuntimeException {
    public BadgeNotFoundException(String message) {
        super(message);
    }
}
