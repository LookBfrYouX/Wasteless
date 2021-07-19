package com.navbara_pigeons.wasteless.enums;

import com.navbara_pigeons.wasteless.entity.MarketListing;

public enum MarketListingSortByOption implements SortByOption {
  title,
  created,
  displayPeriodEnd;

  @Override
  public Class<?> getEntity() {
    return MarketListing.class;
  }
}
