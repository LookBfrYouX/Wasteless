package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.BusinessRegistrationException;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

public class BusinessServiceValidation {
  /**
   * Validates business creation
   * @param business business to validate
   * @param  currentDate LocalDate; used for primary business admin age restriction
   * @throws BusinessRegistrationException if business is invalid
   */
  public static void validate(Business business, LocalDate currentDate) throws BusinessRegistrationException {
    User primaryBusinessAdmin = null;
    for(User user: business.getAdministrators()) {
      if (user.getId() == business.getPrimaryAdministratorId()) {
        primaryBusinessAdmin = user;
        break;
      }
    }

    if (primaryBusinessAdmin == null) {
      throw new BusinessRegistrationException("Business does not have a primary administrator");
    }

    if (primaryBusinessAdmin.getDateOfBirth().isAfter(currentDate.minusYears(16))) {
      throw new BusinessRegistrationException("User must be over 16 years old to be a primary business administrator");
    }
  }
}
