package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.exception.InventoryRegistrationException;
import java.time.LocalDate;

public class InventoryServiceValidation {
  /**
   * Checks if a date is after another. for checking the expiration is after current date.
   *
   * @return true if the product is valid
   */
  protected static boolean date1AfterDate2(LocalDate date1, LocalDate date2) {
    if (date1 == null || date2 == null) {
      return false;
    }
    return date1.isAfter(date2);
  }

  /**
   * Checks if date orders are valid, current date before expiry etc. passing
   *
   * @param inventory
   */
  public static void datesValid(InventoryItem inventory)
      throws InventoryRegistrationException {
    if (InventoryServiceValidation
        .date1AfterDate2(inventory.getManufactured(), inventory.getBestBefore())) {
      throw new InventoryRegistrationException("Best before date must be after manufacture date");
    }
    if (InventoryServiceValidation
        .date1AfterDate2(inventory.getBestBefore(), inventory.getSellBy())) {
      throw new InventoryRegistrationException("Sell by date must be after best before date");
    }
    if (InventoryServiceValidation
        .date1AfterDate2(inventory.getSellBy(), inventory.getExpires())) {
      throw new InventoryRegistrationException("Expiry must be after the sell by date");
    }
  }

  /**
   * Checks if quantity is valid: positive
   *
   * @param quantity quantity to check
   */
  public static void quantityValid(long quantity) throws InventoryRegistrationException {
    if (quantity < 0) {
      throw new InventoryRegistrationException("quantity cannot be negative");
    }

  }

  /**
   * performs all checks on Inventory item
   *
   * @param inventory user input and calculated fields to check
   */
  public static void isInventoryItemValid(InventoryItem inventory)
      throws InventoryRegistrationException {
    InventoryServiceValidation.datesValid(inventory);
    InventoryServiceValidation.quantityValid(inventory.getQuantity());
  }
}