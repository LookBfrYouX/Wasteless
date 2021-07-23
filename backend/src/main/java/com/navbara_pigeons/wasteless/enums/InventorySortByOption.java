package com.navbara_pigeons.wasteless.enums;

import com.navbara_pigeons.wasteless.entity.InventoryItem;

public enum InventorySortByOption implements SortByOption {
  quantity,
  pricePerItem,
  totalPrice,
  manufactured,
  sellBy,
  name; // From Product entity

  @Override
  public Class<?> getEntity() {
    return InventoryItem.class;
  }
}
