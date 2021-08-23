package com.navbara_pigeons.wasteless.exception;

public class ListingNotFoundException extends Exception {

  public ListingNotFoundException() {
    super();
  }

  public ListingNotFoundException(long listingId) {
    super("Listing with ID " + listingId + " could not be found");
  }

}
