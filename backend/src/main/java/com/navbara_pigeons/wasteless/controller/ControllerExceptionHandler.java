package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(InsufficientPrivilegesException.class)
    public ResponseEntity<String> handleConflict(InsufficientPrivilegesException exc) {
        log.error("UNAUTHORISED ACTION: 403 - " + exc.getMessage());
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(403));
    }

    @ExceptionHandler(BusinessNotFoundException.class)
    public ResponseEntity<String> handleBusinessNotFound(BusinessNotFoundException exc) {
        log.error("BUSINESS NOT FOUND: 406 - " + exc.getMessage());
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(406));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException exc) {
        log.error("USER NOT FOUND: 406 - " + exc.getMessage());
        return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(406));
    }

}
