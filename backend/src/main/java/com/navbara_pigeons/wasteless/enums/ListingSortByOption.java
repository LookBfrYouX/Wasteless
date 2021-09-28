package com.navbara_pigeons.wasteless.enums;

import com.navbara_pigeons.wasteless.entity.Listing;

public enum ListingSortByOption implements SortByOption {
  QUANTITY("quantity"),
  PRICE("price"),
  CREATED("created"),
  CLOSES("closes"),
  CITY("inventoryItem.business.address.city"),
  NAME("inventoryItem.product.name"); // From Product entity

  @Override
  public Class<?> getEntity() {
    return Listing.class;
  }

  String keyPath;

  /**
   * Constructor that takes in key path
   * @param keyPath key path for the enum value
   */
  ListingSortByOption(String keyPath) {
    this.keyPath = keyPath;
  }

  /**
   * Gets key path; path to the property to use in sorting
   * @return key path
   */
  public String getKeyPath() {
    return keyPath;
  }
}
