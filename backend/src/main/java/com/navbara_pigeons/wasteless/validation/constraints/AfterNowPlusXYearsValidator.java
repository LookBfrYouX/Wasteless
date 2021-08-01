package com.navbara_pigeons.wasteless.validation.constraints;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * Checks that the given dates are not past a certain point x years in the future (or past), or vice
 * versa. Boundaries are inclusive.
 * <p>
 * This has been tested with LocalDate and ZonedDateTime.
 * <p>
 * The 'x' in the diagram below indicates areas that will cause the validation to fail:
 * <pre>
 * The Past-----------------|-----------Now------------|--------------The Future
 * BeforeNowPlusXYears(-ve) | xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx
 *                      BeforeNowPlusXYears(+ve)       | xxxxxxxxxxxxxxxxxxxxxxx
 * xxxxxxxxxxxxxxxxxxxxxxxx |          AfterNowPlusXYears(-ve)
 * xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx | AfterNowPlusXYears(+ve)
 * </pre>
 */
public class AfterNowPlusXYearsValidator implements
    ConstraintValidator<AfterNowPlusXYears, Temporal> {

  int years;

  @Override
  public void initialize(AfterNowPlusXYears afterNowPlusXYears) {
    years = afterNowPlusXYears.years();
  }

  @Override
  public boolean isValid(Temporal value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    } else {
      return
          value.until(ZonedDateTime.now(ZoneId.systemDefault()).plusYears(years), ChronoUnit.DAYS)
              <= 0;
    }
  }
}