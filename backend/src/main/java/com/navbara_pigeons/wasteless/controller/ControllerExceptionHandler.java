package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.exception.*;

import javax.management.InvalidAttributeValueException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.ArrayList;

/**
 * This class handles all custom controller exceptions and returns the appropriate response entity
 * for each. Error logging for controllers is also handled in this class. It does NOT handle Spring
 * Security exceptions however.
 */
@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

  /**
   * This is the exception handler for InsufficientPrivilegesExceptions.
   *
   * @param exc The thrown exception
   * @return ResponseEntity with the exception message
   */
  @ExceptionHandler(InsufficientPrivilegesException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<String> handleInsufficientPrivilegesException(
      InsufficientPrivilegesException exc) {
    log.error("UNAUTHORISED ACTION: 403 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.FORBIDDEN);
  }

  /**
   * This is the exception handler for BusinessNotFoundExceptions.
   *
   * @param exc The thrown exception
   * @return ResponseEntity with the exception message
   */
  @ExceptionHandler(BusinessNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  public ResponseEntity<String> handleBusinessNotFoundException(BusinessNotFoundException exc) {
    log.error("BUSINESS NOT FOUND: 406 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.NOT_ACCEPTABLE);
  }

  /**
   * This is the exception handler for UserNotFoundExceptions.
   *
   * @param exc The thrown exception
   * @return ResponseEntity with the exception message
   */
  @ExceptionHandler(UserNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exc) {
    log.error("USER NOT FOUND: 406 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.NOT_ACCEPTABLE);
  }

  /**
   * Whenever MaxUploadSizeExceededException is thrown, this class will be run
   *
   * @return Response to the user (BAD_REQUEST)
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleMaxSizeException() {
    return new ResponseEntity<>("File too large!", HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(BadCredentialsException.class)
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<String> handleBadCredentialsException(BadCredentialsException exc) {
    log.error("BAD CREDENTIALS: 403 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(NotAcceptableException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ResponseEntity<String> handleNotAcceptableException(NotAcceptableException exc) {
    log.error("NOT ACCEPTABLE: 409 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.CONFLICT);
  }

  @ExceptionHandler(NumberFormatException.class)
  @ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
  public ResponseEntity<String> handleNumberFormatException(NumberFormatException exc) {
    log.error("NUMBER FORMAT ERROR: 406 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.NOT_ACCEPTABLE);
  }

  @ExceptionHandler(ProductRegistrationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleProductRegistrationException(
      ProductRegistrationException exc) {
    log.error("PRODUCT REGISTRATION ERROR: 400 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InventoryItemNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleInventoryItemNotFoundException(
      InventoryItemNotFoundException exc) {
    log.error("INVENTORY ITEM ERROR: 406 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
  }

  /**
   * This is the exception handler for InventoryRegistrationExceptions.
   *
   * @param exc The thrown exception
   * @return ResponseEntity with the exception message
   */
  @ExceptionHandler(InventoryRegistrationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleInventoryRegistrationException(
      InventoryRegistrationException exc) {
    log.error("INVENTORY REGISTRATION EXCEPTION: 400 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
  }

  /**
   * This is the exception handler for ProductNotFoundExceptions.
   *
   * @param exc The thrown exception
   * @return ResponseEntity with the exception message
   */
  @ExceptionHandler(ProductNotFoundException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleProductNotFound(ProductNotFoundException exc) {
    log.error("PRODUCT NOT FOUND: 406 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidAttributeValueException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<String> handleInvalidAttributeValueException(
      InvalidAttributeValueException exc) {
    log.error("SEARCH QUERY ERROR: 500 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(InvalidPaginationInputException.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<String> handleInvalidPaginationInputException(
      InvalidPaginationInputException exc) {
    log.error("PAGINATION ERROR: 500 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(UserAlreadyExistsException.class)
  @ResponseStatus(code = HttpStatus.CONFLICT, reason = "Email address already in use")
  public void handleUserAlreadyExistsException(UserAlreadyExistsException exc) {
    log.error("USER ALREADY EXISTS ERROR: 409 - " + exc.getMessage());
  }

  @ExceptionHandler(UserRegistrationException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Bad Request")
  public void handleUserRegistrationException(UserRegistrationException exc) {
    log.error("USER REGISTRATION ERROR: 400 - " + exc.getMessage());
  }

  @ExceptionHandler(BusinessRegistrationException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Bad Request")
  public void handleBusinessRegistrationException(BusinessRegistrationException exc) {
    log.error("BUSINESS REGISTRATION ERROR: 400 - " + exc.getMessage());
  }

  @ExceptionHandler(BusinessTypeException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Bad Request")
  public void handleBusinessTypeException(BusinessTypeException exc) {
    log.error("BUSINESS TYPE ERROR: 400 - " + exc.getMessage());
  }

  @ExceptionHandler(AddressValidationException.class)
  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Bad address given")
  public void handleAddressValidationException(AddressValidationException exc) {
    log.error("ADDRESS VALIDATION ERROR: 400 - " + exc.getMessage());
  }

  @ExceptionHandler(UserAuthenticationException.class)
  @ResponseStatus(
      code = HttpStatus.BAD_REQUEST,
      reason = "Failed login attempt, email or password incorrect")
  public void handleUserAuthenticationException(UserAuthenticationException exc) {
    log.error("FAILED LOGIN: 400 - " + exc.getMessage());
  }

  @ExceptionHandler(ListingValidationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleListingValidationException(ListingValidationException exc) {
    log.error("BAD REQUEST: 400 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(InvalidMarketListingSectionException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleInvalidMarketListingSectionException(InvalidMarketListingSectionException exc) {
    log.error("BAD REQUEST: 400 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<String> handleGeneralException(Exception exc) {
    log.error("CRITICAL ERROR: 500 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
  }


  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exc) {
    log.error("ENTITY VALIDATION EXCEPTION: 400 - " + exc.getMessage());
    ArrayList<String> errors = new ArrayList<>();
    for(FieldError error: exc.getBindingResult().getFieldErrors()) {
      errors.add(error.getField() + ": " + error.getDefaultMessage());
    }

    for(ObjectError error: exc.getBindingResult().getGlobalErrors()) {
      errors.add(error.getDefaultMessage());
    }

    String message = "Invalid entity received:";
    for(String msg: errors) {
      message += "\n- " + msg;
    }

    return new ResponseEntity<>(message, HttpStatus.BAD_REQUEST);
  }
}
