package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.Product;

public class ProductServiceValidation {

  /**
   * Checks if the given string is null, empty, or contains whitespace only
   *
   * @param str value string to check
   * @return true if the given string is null, empty, or contains whitespace only
   */
  public static boolean isNullOrTrimmedEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }

  /**
   * Checks if the given product is valid
   *
   * @param product product to be apart of a business category or inventory
   * @return true if the product is valid
   */
  public static boolean isProductValid(Product product) {
    if (isNullOrTrimmedEmpty(product.getName())) {
      return false;
    }
    return true;
  }
}
