package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.Product;


public class ProductServiceValidation {

  /**
   * Checks if the required fields are not empty
   *
   * @param product product to be a part of a business category or inventory
   * @return true if the product is valid
   */
  public static boolean requiredFieldsNotEmpty(Product product) {
    for (String el : new String[]{product.getName(), product.getManufacturer()}) {
//      System.out.println(el);
//      System.out.println(ValidationHelper.isNullOrTrimmedEmpty(el));
      if (ValidationHelper.isNullOrTrimmedEmpty(el)) {
        return false;
      }
    }

//    if (product.getRecommendedRetailPrice().isNaN())

    return true;
  }

  /**
   * Checks if price is valid: positive and less than 10000
   *
   * @param price price to check
   * @return true if valid
   */
  public static boolean priceIsValid(Double price) {
    return !price.isNaN() && price > 0 && price < 10000;
  }
}
