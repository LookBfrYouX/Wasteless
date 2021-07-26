package com.navbara_pigeons.wasteless.enums;

import com.navbara_pigeons.wasteless.entity.MarketListing;

public enum MarketListingSortByOption implements SortByOption {
  title,
  created,
  displayPeriodEnd,
  suburb,
  city;

  @Override
  public Class<?> getEntity() {
    return MarketListing.class;
  }
}
