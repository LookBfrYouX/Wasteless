package com.navbara_pigeons.wasteless.exception;

public class ProductNotFoundException extends Exception {

  public ProductNotFoundException() {
    super();
  }

  public ProductNotFoundException(long productId) {
    super("Product with ID " + productId + " could not be found");
  }
}
