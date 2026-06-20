package com.learning.api.exception;

/**
 * Thrown when trying to register a user with an email that already exists.
 */
public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}
