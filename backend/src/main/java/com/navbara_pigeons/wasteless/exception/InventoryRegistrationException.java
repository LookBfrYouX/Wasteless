package com.navbara_pigeons.wasteless.exception;

import com.navbara_pigeons.wasteless.entity.Inventory;

public class InventoryRegistrationException extends Exception{
  public InventoryRegistrationException() {
    super();
  }

  public InventoryRegistrationException(String message) {
    super(message);
  }
}
