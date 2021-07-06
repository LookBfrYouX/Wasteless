package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessRegistrationException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.InventoryRegistrationException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.NotAcceptableException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.ProductRegistrationException;
import com.navbara_pigeons.wasteless.exception.UserAlreadyExistsException;
import com.navbara_pigeons.wasteless.exception.UserAuthenticationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserRegistrationException;
import javax.management.InvalidAttributeValueException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

/**
 * This class handles all custom controller exceptions and returns the appropriate response entity
 * for each. Error logging for controllers is also handled in this class. It does NOT handle Spring
 * Security exceptions however.
 */
@ControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

  /**
   * This is the exception handler for InsufficientPrivilegesExceptions.
   *
   * @param exc The thrown exception
   * @return ResponseEntity with the exception message
   */
  @ExceptionHandler(InsufficientPrivilegesException.class)
  public ResponseEntity<String> handleInsufficientPrivilegesException(
      InsufficientPrivilegesException exc) {
    log.error("UNAUTHORISED ACTION: 403 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(403));
  }

  /**
   * This is the exception handler for BusinessNotFoundExceptions.
   *
   * @param exc The thrown exception
   * @return ResponseEntity with the exception message
   */
  @ExceptionHandler(BusinessNotFoundException.class)
  public ResponseEntity<String> handleBusinessNotFoundException(BusinessNotFoundException exc) {
    log.error("BUSINESS NOT FOUND: 406 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(406));
  }

  /**
   * This is the exception handler for UserNotFoundExceptions.
   *
   * @param exc The thrown exception
   * @return ResponseEntity with the exception message
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException exc) {
    log.error("USER NOT FOUND: 406 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(406));
  }

  /**
   * Whenever MaxUploadSizeExceededException is thrown, this class will be run
   *
   * @return Response to the user (BAD_REQUEST)
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<String> handleMaxSizeException() {
    return new ResponseEntity<>("File too large!", HttpStatus.BAD_REQUEST);
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

  @ExceptionHandler(ProductRegistrationException.class)
  public ResponseEntity<String> handleProductRegistrationException(
      ProductRegistrationException exc) {
    log.error("PRODUCT REGISTRATION ERROR: 400 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(400));
  }

  @ExceptionHandler(InventoryItemNotFoundException.class)
  public ResponseEntity<String> handleInventoryItemNotFoundException(
      InventoryItemNotFoundException exc) {
    log.error("INVENTORY ITEM ERROR: 406 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(406));
  }

  /**
   * This is the exception handler for InventoryRegistrationExceptions.
   *
   * @param exc The thrown exception
   * @return ResponseEntity with the exception message
   */
  @ExceptionHandler(InventoryRegistrationException.class)
  public ResponseEntity<String> handleInventoryRegistrationException(
      InventoryRegistrationException exc) {
    log.error("INVENTORY REGISTRATION EXCEPTION: 400 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(400));
  }

  /**
   * This is the exception handler for ProductNotFoundExceptions.
   *
   * @param exc The thrown exception
   * @return ResponseEntity with the exception message
   */
  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<String> handleProductNotFound(ProductNotFoundException exc) {
    log.error("PRODUCT NOT FOUND: 406 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(400));
  }

  @ExceptionHandler(InvalidAttributeValueException.class)
  public ResponseEntity<String> handleInvalidAttributeValueException(
      InvalidAttributeValueException exc) {
    log.error("SEARCH QUERY ERROR: 500 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(500));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(
      InvalidAttributeValueException exc) {
    log.error("SEARCH QUERY ERROR: 500 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(500));
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
  @ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "Failed login attempt, email or password incorrect")
  public void handleUserAuthenticationException(UserAuthenticationException exc) {
    log.error("FAILED LOGIN: 400 - " + exc.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleGeneralException(Exception exc) {
    log.error("CRITICAL ERROR: 500 - " + exc.getMessage());
    exc.printStackTrace();
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(500));
  }


  @ExceptionHandler(ListingValidationException.class)
  public ResponseEntity<String> handleListingValidationException(ListingValidationException exc) {
    log.error("BAD REQUEST: 400 - " + exc.getMessage());
    return new ResponseEntity<>(exc.getMessage(), HttpStatus.valueOf(400));
  }
}
