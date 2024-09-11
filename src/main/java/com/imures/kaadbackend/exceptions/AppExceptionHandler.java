package com.imures.kaadbackend.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.persistence.EntityNotFoundException;

import java.nio.file.AccessDeniedException;
import java.time.OffsetDateTime;

@ControllerAdvice
public class AppExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AppExceptionHandler.class);

    @ExceptionHandler(value = {EntityNotFoundException.class})
    public ResponseEntity<Object> handleNotFoundException(EntityNotFoundException ex) {
        logger.error("EntityNotFoundException: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorMessage(OffsetDateTime.now(), ex.getMessage()),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(value = {NullValueException.class})
    public ResponseEntity<Object> handleNullValueException(NullValueException ex) {
        logger.error("NullValueException: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorMessage(OffsetDateTime.now(), ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

//    @ExceptionHandler(value = {SignatureException.class})
//    public ResponseEntity<Object> handleSignatureException(SignatureException ex) {
//        logger.error("SignatureException: {}", ex.getMessage());
//        return new ResponseEntity<>(
//                new ErrorMessage(OffsetDateTime.now(), ex.getMessage()),
//                HttpStatus.FORBIDDEN
//        );
//    }
//
//    @ExceptionHandler(value = {ExpiredJwtException.class})
//    public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex) {
//        logger.error("ExpiredJwtException: {}", ex.getMessage());
//        return new ResponseEntity<>(
//                new ErrorMessage(OffsetDateTime.now(), ex.getMessage()),
//                HttpStatus.FORBIDDEN
//        );
//    }

    @ExceptionHandler(value = {AccessDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex) {
        logger.error("AccessDeniedException: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorMessage(OffsetDateTime.now(), ex.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        logger.error("Exception: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorMessage(OffsetDateTime.now(), ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }

}