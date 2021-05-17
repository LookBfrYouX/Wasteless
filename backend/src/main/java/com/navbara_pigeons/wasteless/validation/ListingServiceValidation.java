package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.Listing;
import java.util.ArrayList;

public class ListingServiceValidation {


  public static boolean isListingValid(Listing listing) {
    // Check the given id exists
    if (listing.getInventoryItem() != null) {
      return false;
    }

    // TODO: merge get endpoint into this branch to be able to call this method below
    // Quantity must be above 0 and below the remaining quantity
    long remainingQuantity = listing.getInventoryItem().getQuantity();
//    ArrayList<Listing> currentListings = listing.getInventoryItem().getBusiness().getInventory().getListings();
//    for (Listing loopListing : currentListings) {
//      if (loopListing.getId() == listing.getId()) {
//        remainingQuantity -= loopListing.getQuantity();
//      }
//    }
    if (listing.getQuantity() <= 0 || listing.getQuantity() > remainingQuantity) {
      return false;
    }
    return true;
  }
}