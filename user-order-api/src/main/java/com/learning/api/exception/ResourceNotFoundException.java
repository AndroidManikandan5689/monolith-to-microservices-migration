package com.learning.api.exception;

/**
 * Thrown when a requested resource (User, Order) does not exist in the database.
 */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
