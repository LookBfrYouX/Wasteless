package com.navbara_pigeons.wasteless.enums;

import com.navbara_pigeons.wasteless.entity.Product;

public enum ProductSortByOption implements SortByOption {
  name,
  manufacturer,
  recommendedRetailPrice,
  created;

  @Override
  public Class<?> getEntity() {
    return Product.class;
  }
}
