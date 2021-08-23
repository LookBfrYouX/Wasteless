package com.navbara_pigeons.wasteless.enums;

import com.navbara_pigeons.wasteless.entity.Listing;

public enum ListingSortByOption implements SortByOption {
  quantity,
  price,
  created,
  closes,
  city,
  name; // From Product entity

  @Override
  public Class<?> getEntity() {
    return Listing.class;
  }
}
