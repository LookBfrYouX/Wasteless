package com.navbara_pigeons.wasteless.validation.constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Validator for string enum
 * Code copied from https://stackoverflow.com/a/18209990
 */
public class StringEnumerationValidator implements ConstraintValidator<StringEnumeration, String> {

  private Set<String> AVAILABLE_ENUM_NAMES;

  /**
   * Gets the toString values of each enum value
   * @param e enum class
   * @return Set of enum values
   */
  public static Set<String> getNamesSet(Class<? extends Enum<?>> e) {
    Enum<?>[] enums = e.getEnumConstants();
    String[] names = new String[enums.length];
    for (int i = 0; i < enums.length; i++) {
      names[i] = enums[i].toString();
    }
    return new HashSet<String>(Arrays.asList(names));
  }

  @Override
  public void initialize(StringEnumeration stringEnumeration) {
    Class<? extends Enum<?>> enumSelected = stringEnumeration.enumClass();
    AVAILABLE_ENUM_NAMES = getNamesSet(enumSelected);
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (value == null) {
      return true;
    } else {
      return AVAILABLE_ENUM_NAMES.contains(value);
    }
  }
}