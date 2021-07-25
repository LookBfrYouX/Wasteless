package com.navbara_pigeons.wasteless.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 *
 */
public class NotTooDistantFutureValidator implements ConstraintValidator<NotTooDistantFuture, LocalDate> {
  @Override
  public boolean isValid(LocalDate value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    } else {
      return LocalDate.now().plusYears(100).isBefore(value);
    }
  }
}