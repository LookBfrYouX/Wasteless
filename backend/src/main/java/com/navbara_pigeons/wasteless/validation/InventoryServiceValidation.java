package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.exception.InventoryRegistrationException;

import java.time.LocalDate;
import java.util.AbstractMap;
import java.util.List;

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
    List<AbstractMap.SimpleEntry<String, LocalDate>> ordering = List.of(
        new AbstractMap.SimpleEntry<>("Manufactured", inventory.getManufactured()),
        new AbstractMap.SimpleEntry<>("Best Before", inventory.getBestBefore()),
        new AbstractMap.SimpleEntry<>("Sell By", inventory.getSellBy()),
        new AbstractMap.SimpleEntry<>("Expiry", inventory.getExpires())
    );

    // Dates in the list must be in order
    // Because some elements can be null, cannot just compare dates that are next
    // to each other in the list. Hence, must compare each element with every other element
    for(int i = 0; i < ordering.size() - 1; i++) {
      for(int j = i + 1; j < ordering.size(); j++) {
        if (InventoryServiceValidation.date1AfterDate2(
            ordering.get(i).getValue(),
            ordering.get(j).getValue()
        )) {
          throw new InventoryRegistrationException(
              String.format("%s date must be after %s date",
                  ordering.get(i).getKey(),
                  ordering.get(j).getKey()
              )
          );
        }
      }
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