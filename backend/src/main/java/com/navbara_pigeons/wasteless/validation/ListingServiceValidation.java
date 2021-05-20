package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import java.util.ArrayList;
import java.util.List;

public class ListingServiceValidation {


  public static boolean isListingValid(Listing listing) {
    // Check the given id exists
    if (listing.getInventoryItem() == null) {
      return false;
    }

    // Quantity must be above 0 and below the remaining quantity
    long remainingQuantity = listing.getInventoryItem().getQuantity();
    List<InventoryItem> inventory = listing.getInventoryItem().getBusiness().getInventory();
    for (InventoryItem inventoryItem : inventory) {
      for (Listing listing1 : inventoryItem.getListings()) {
        if (listing1.getId() == listing.getId()) {
          remainingQuantity -= listing1.getQuantity();
        }
      }
    }
    if (listing.getQuantity() <= 0 || listing.getQuantity() > remainingQuantity) {
      return false;
    }
    return true;
  }
}