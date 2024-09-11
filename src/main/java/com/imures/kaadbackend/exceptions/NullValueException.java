package com.imures.kaadbackend.exceptions;

import org.springframework.validation.Errors;

public class NullValueException extends RuntimeException {

    private Errors errors;

    public NullValueException(Errors errors) {
        this.errors = errors;
    }

    public NullValueException(String message) {
        super(message);
    }

    public NullValueException(String message, Errors errors) {
        super(message);
        this.errors = errors;
    }

}
