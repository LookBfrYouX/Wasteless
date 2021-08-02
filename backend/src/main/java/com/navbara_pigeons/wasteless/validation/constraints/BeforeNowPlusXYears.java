package com.navbara_pigeons.wasteless.validation.constraints;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.CONSTRUCTOR;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Checks that the given dates are not past a certain point x years in the future (or past), or vice
 * versa. Boundaries are inclusive.
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
@Documented
@Constraint(validatedBy = BeforeNowPlusXYearsValidator.class)
@Target({METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, CONSTRUCTOR})
@Retention(RUNTIME)
public @interface BeforeNowPlusXYears {

  String message() default
      "{com.navbara_pigeons.wasteless.validation.constraints.BetweenNowAndXYears." + "message}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

  int years() default 100;
}