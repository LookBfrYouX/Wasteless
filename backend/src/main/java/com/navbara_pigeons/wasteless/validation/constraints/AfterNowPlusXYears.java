package com.navbara_pigeons.wasteless.validation.constraints;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for string enum; ensures string matches `toString` value of an enum class
 * Code copied from https://stackoverflow.com/a/18209990
 */
@Documented
@Constraint(validatedBy = AfterNowPlusXYearsValidator.class)
@Target({ METHOD, FIELD, ANNOTATION_TYPE, PARAMETER, CONSTRUCTOR })
@Retention(RUNTIME)
public @interface AfterNowPlusXYears {
  String message() default "{com.navbara_pigeons.wasteless.validation.constraints.OutsideXYears." + "message}";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
  int years() default 100;
}