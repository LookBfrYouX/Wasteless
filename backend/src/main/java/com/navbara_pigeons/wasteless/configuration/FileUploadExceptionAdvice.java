package com.navbara_pigeons.wasteless.configuration;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Global error handling middleware for file uploads
 */
@ControllerAdvice
public class FileUploadExceptionAdvice extends ResponseEntityExceptionHandler {

  /**
   * Whenever MaxUploadSizeExceededException is thrown, this class will be run
   *
   * @return Response to the user (BAD_REQUEST)
   */
  @ExceptionHandler(MaxUploadSizeExceededException.class)
  public ResponseEntity<String> handleMaxSizeException() {
    return new ResponseEntity<>("File too large!", HttpStatus.BAD_REQUEST);
  }
}
