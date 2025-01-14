package com.imures.kaadbackend.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;

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

    @ExceptionHandler(value = {ExpiredJwtException.class})
    public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex) {
        logger.error("ExpiredJwtException: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorMessage(OffsetDateTime.now(), ex.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(value = {AuthorizationDeniedException.class})
    public ResponseEntity<Object> handleAuthorizationDeniedException(AuthorizationDeniedException ex) {
        logger.error("AuthorizationDeniedException: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorMessage(OffsetDateTime.now(), ex.getMessage()),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(value = {MissingServletRequestPartException.class})
    public ResponseEntity<Object> handleMissingServletRequestPartException(MissingServletRequestPartException ex) {
        logger.error("MissingServletRequestPartException: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorMessage(OffsetDateTime.now(), ex.getMessage()),
                HttpStatus.BAD_REQUEST
        );
    }


    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<Object> handleAllExceptions(Exception ex) {
        logger.error("Exception: {}", ex.getMessage());
        return new ResponseEntity<>(
                new ErrorMessage(OffsetDateTime.now(), "Oops! Something went wrong!"),
                HttpStatus.BAD_REQUEST
        );
    }

}