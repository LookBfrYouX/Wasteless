package com.pigeon.wasteless.validation;

import java.util.Arrays;
import java.util.List;

public class BusinessServiceValidation {

  /**
   * Returns false if the business type doesnt pass validation
   *
   * @param businessType Businesses Type
   */
  public static boolean isBusinessTypeValid(String businessType) {
    List<String> businessTypes = Arrays
        .asList("Accommodation and Food Services", "Retail Trade", "Charitable organisation",
            "Non-profit organisation");
    return businessTypes.contains(businessType);
  }
}
