package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.User;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.regex.Pattern;

public class UserServiceValidation {

  /**
   * Returns false if the password doesnt pass validation: At least 8 chars Contains at least one
   * digit, contains at least one lower alpha char and one upper alpha char, does not contain
   * space/tab/etc.
   *
   * @param password Users password
   */
  public static boolean isPasswordValid(String password) {
    String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
    return Pattern.matches(passwordRegex, password);
  }

  /**
   * Returns false if the email doesnt pass validation
   *
   * @param email Users email
   */
  public static boolean isEmailValid(String email) {
    String emailRegex = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    return Pattern.matches(emailRegex, email);
  }

  /**
   * Returns true if a users date of birth is a valid date
   *
   * @param dob Users date of birth
   */
  public static boolean isDobValid(String dob) {
    // Check date is received correctly
    try {
      LocalDate.parse(dob).format(DateTimeFormatter.ISO_LOCAL_DATE);
    } catch (DateTimeParseException exc) {
      return false;
    }
    return true;
  }

  /**
   * Checks if the given string is null, empty, or contains whitespace only
   * @param str
   * @return true if the given string is null, empty, or contains whitespace only
   */
  public static boolean isNullOrTrimmedEmpty(String str) {
    return str == null || str.trim().isEmpty();
  }

  /**
   * Returns false if required sql fields are null/empty
   *
   * @param user User
   */
  public static boolean isUserValid(User user) {
    // Checks user fields are not null/empty
    Address address = user.getAddress();
    for(String val: new String[]{
            user.getFirstName(), user.getMiddleName(), user.getLastName(), user.getNickname(),
            address.getStreetNumber(), address.getStreetName(), address.getPostcode(),
            address.getCity(), address.getRegion(), address.getCountry()
    }) {
      if (UserServiceValidation.isNullOrTrimmedEmpty(val)) return false;
    }

    return true;
  }
}
