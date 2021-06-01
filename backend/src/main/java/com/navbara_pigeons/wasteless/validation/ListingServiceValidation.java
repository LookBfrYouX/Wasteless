package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import java.util.List;

public class ListingServiceValidation {


  public static void isListingValid(Listing listing) throws ListingValidationException {
    // Check the given id exists
    if (listing.getInventoryItem() == null) {
      throw new ListingValidationException("Inventory item not found");
    }
    // Check price is not negative or null
    if (listing.getPrice() == null || listing.getPrice() <= 0) {
      throw new ListingValidationException("Price must be greater than 0");
    }

    // Quantity must be above 0 and below the remaining quantity
    long remainingQuantity = listing.getInventoryItem().getQuantity();
    List<InventoryItem> inventory = listing.getInventoryItem().getBusiness().getInventory();
    for (InventoryItem inventoryItem : inventory) {
      if (inventoryItem.getId() == listing.getInventoryItem().getId()) {
        for (Listing listing1 : inventoryItem.getListings()) {
          remainingQuantity -= listing1.getQuantity();
        }
      }
    }
    if (listing.getQuantity() <= 0 || listing.getQuantity() > remainingQuantity) {
      throw new ListingValidationException(
          "Quantity must be less than or equal to the amount left in the inventory and greater than 0");
    }
  }
}