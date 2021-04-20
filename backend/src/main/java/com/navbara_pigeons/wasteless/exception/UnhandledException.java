package com.navbara_pigeons.wasteless.exception;

/**
 * To be thrown on ambiguous errors
 */
public class UnhandledException extends Exception {

  public UnhandledException() {
    super();
  }

  public UnhandledException(String message) {
    super(message);
  }
}
