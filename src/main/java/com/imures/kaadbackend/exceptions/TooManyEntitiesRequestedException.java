package com.imures.kaadbackend.exceptions;

public class TooManyEntitiesRequestedException extends RuntimeException{
    public TooManyEntitiesRequestedException(String message) {
        super(message);
    }
}
