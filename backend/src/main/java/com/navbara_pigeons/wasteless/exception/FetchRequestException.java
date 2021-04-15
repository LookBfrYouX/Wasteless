package com.navbara_pigeons.wasteless.exception;

public class FetchRequestException extends Throwable {
    public FetchRequestException(Exception e) {
      super(e);
    }

    public FetchRequestException(String message) {
    super(message);
  }
}
