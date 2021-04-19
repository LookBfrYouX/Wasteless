package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Product;

public class ProductServiceValidation {
  /**
   * Checks if the required fields are not empty
   * @param product product to be a part of a business category or inventory
   * @return true if the product is valid
   */
  public static boolean requiredFieldsNotEmpty(Product product) {
    if (ValidationHelper.isNullOrTrimmedEmpty(product.getName())) {
      return false;
    }
    return true;
  }
}
