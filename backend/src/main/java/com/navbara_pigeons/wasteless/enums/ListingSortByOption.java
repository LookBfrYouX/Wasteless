package com.navbara_pigeons.wasteless.enums;

import com.navbara_pigeons.wasteless.entity.Listing;

public enum ListingSortByOption implements SortByOption {
  QUANTITY("quantity"),
  PRICE("price"),
  CREATED("created"),
  CLOSES("closes"),
  CITY("inventoryItem.business.address.city", "city"),
  NAME("inventoryItem.product.name", "name");

  @Override
  public Class<?> getEntity() {
    return Listing.class;
  }

  String keyPath; // used by ListingServiceImpl.searchListings (all listings)
  String propertyName; // used by ListingQueryBuilder (listings for a particular business. Could be done with a
  // regex by getting last set of characters after the dot, but that's just more testing

  /**
   * Constructor that takes in key path
   * @param name key path AND property name for the enum value
   */
  ListingSortByOption(String name) {
    this.keyPath = name;
    this.propertyName = name;
  }

  /**
   * Constructor that takes in key path and property name
   * @param keyPath key path for the enum value
   * @param propertyName name of the property
   */
  ListingSortByOption(String keyPath, String propertyName) {
    this.keyPath = keyPath;
    this.propertyName = propertyName;
  }

  /**
   * Gets key path; path to the property to use in sorting
   * @return key path
   */
  public String getKeyPath() {
    return keyPath;
  }

  /**
   * Name of the property in the table
   * @return property name
   */
  public String getPropertyName() {
    return propertyName;
  }
}
