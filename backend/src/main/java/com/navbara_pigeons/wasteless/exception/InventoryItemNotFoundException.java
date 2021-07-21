package com.navbara_pigeons.wasteless.exception;

public class InventoryItemNotFoundException extends Exception {

  public InventoryItemNotFoundException() {
    super();
  }

  public InventoryItemNotFoundException(long inventoryItemId) {
    super("Inventory Item with ID " + inventoryItemId + " could not be found");
  }

}
