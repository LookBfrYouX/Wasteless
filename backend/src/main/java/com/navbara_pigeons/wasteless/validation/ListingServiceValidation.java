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
    List<InventoryItem> currentListings = listing.getInventoryItem().getBusiness().getInventory();
    for (InventoryItem loopItem : currentListings) {
      for (Listing loopListing : loopItem.getListings()) {
        if (loopListing.getId() == listing.getId()) {
          remainingQuantity -= loopListing.getQuantity();
        }
      }
    }
    if (listing.getQuantity() <= 0 || listing.getQuantity() > remainingQuantity) {
      return false;
    }
    return true;
  }
}