package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * This class handles all custom controller exceptions and returns the appropriate response entity for each.
 * Error logging for controllers is also handled in this class.
 * It does NOT handle Spring Security exceptions however.
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    /**
     * This is the exception handler for InsufficientPrivilegesExceptions.
     * @param exc The thrown exception
     * @return ResponseEntity with the exception message
     */
    @ExceptionHandler(InsufficientPrivilegesException.class)
    public ResponseEntity<String> handleConflict(InsufficientPrivilegesException exc) {
        log.error("UNAUTHORISED ACTION: 403 - " + exc.getMessage());
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(403));
    }

    /**
     * This is the exception handler for BusinessNotFoundExceptions.
     * @param exc The thrown exception
     * @return ResponseEntity with the exception message
     */
    @ExceptionHandler(BusinessNotFoundException.class)
    public ResponseEntity<String> handleBusinessNotFound(BusinessNotFoundException exc) {
        log.error("BUSINESS NOT FOUND: 406 - " + exc.getMessage());
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(406));
    }

    /**
     * This is the exception handler for UserNotFoundExceptions.
     * @param exc The thrown exception
     * @return ResponseEntity with the exception message
     */
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException exc) {
        log.error("USER NOT FOUND: 406 - " + exc.getMessage());
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(406));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException exc) {
        log.error("BAD CREDENTIALS: 403 - " + exc.getMessage());
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(403));
    }

    @ExceptionHandler(NotAcceptableException.class)
    public ResponseEntity<String> handleNotAcceptableException(NotAcceptableException exc) {
        log.error("NOT ACCEPTABLE: 409 - " + exc.getMessage());
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(409));
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<String> handleNumberFormatException(NumberFormatException exc) {
        log.error("NUMBER FORMAT ERROR: 406 - " + exc.getMessage());
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(406));
    }

    @ExceptionHandler(ProductForbiddenException.class)
    public ResponseEntity<String> handleProductForbiddenException(ProductForbiddenException exc) {
        log.error("PRODUCT ERROR: 403 - " + exc.getMessage());
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(403));
    }

    @ExceptionHandler(ProductRegistrationException.class)
    public ResponseEntity<String> handleProductRegistrationException(ProductRegistrationException exc) {
        log.error("PRODUCT REGISTRATION ERROR: 400 - " + exc.getMessage());
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(400));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneralException(Exception exc) {
        log.error("CRITICAL ERROR: 500 - " + exc.getMessage());
        exc.printStackTrace();
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(500));
    }

}
