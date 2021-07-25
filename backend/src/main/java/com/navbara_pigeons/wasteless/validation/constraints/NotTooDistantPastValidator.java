package com.navbara_pigeons.wasteless.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

/**
 *
 */
public class NotTooDistantPastValidator implements ConstraintValidator<NotTooDistantPast, LocalDate> {
  int minYears;

  @Override
  public void initialize(NotTooDistantPast notTooDistantPast) {
    minYears = Integer.parseInt(notTooDistantPast.minYears());
  }

  @Override
  public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    } else {
      return LocalDate.now().minusYears(minYears).isAfter(value);
    }
  }
}