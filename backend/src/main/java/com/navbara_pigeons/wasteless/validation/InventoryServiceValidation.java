package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.entity.Inventory;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.InventoryRegistrationException;

import java.time.LocalDate;

public class InventoryServiceValidation {

  /**
   * Checks if the required fields are not empty
   *
   * @param inventory item to validate
   * @return true if the product is valid
   */
  public static boolean requiredFieldsNotEmpty(CreateInventoryItemDto inventory) {
    return inventory.getExpires() != null;
  }

  protected static boolean date1AfterDate2(LocalDate date1, LocalDate date2) {
    if (date1 == null || date2 == null) return false;
    return date1.isAfter(date2);
  }

  /**
   * Checks if price is valid: positive and less than 10000
   *
   * @param currentDate date to check
   */
  public static void datesValid(CreateInventoryItemDto inventory, LocalDate currentDate) throws InventoryRegistrationException {
    // TODO how do we ensure local date is equal to date of the user?
    if (InventoryServiceValidation.date1AfterDate2(inventory.getManufactured(), currentDate)) {
      throw new InventoryRegistrationException("Manufacture date must be before or equal to today");
    }
    if (InventoryServiceValidation.date1AfterDate2(inventory.getManufactured(), inventory.getBestBefore())) {
      throw new InventoryRegistrationException("Best before date must be after manufacture date");
    }
    if (InventoryServiceValidation.date1AfterDate2(inventory.getBestBefore(), inventory.getSellBy())) {
      throw new InventoryRegistrationException("Sell by date must be after best before date");
    }
    if (InventoryServiceValidation.date1AfterDate2(currentDate, inventory.getExpires())) {
      throw new InventoryRegistrationException("Expiry date must be on or after today");
    }
  }

  /**
   * Checks if price is valid: positive and less than 10000
   *
   * @param price price to check
   * @return if price is valid or null
   */
  public static void priceValid(Double price) throws InventoryRegistrationException {
    if (price == null) return;
    if (price < 0) {
      throw new InventoryRegistrationException("Price cannot be negative");
    } else if (price >= 10000) {
      throw new InventoryRegistrationException("Price cannot be more than 10000");
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

}