package com.navbara_pigeons.wasteless.validation;

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
    // An RFC 5322 compliant regex string for email validation (though actual validation would still be better)
    String emailRegex = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])" +
            "*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4]" +
            "[0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:" +
            "(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";
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
   * Returns false if required sql fields are null/empty
   *
   * @param user User
   */
  public static boolean isUserValid(User user) {
    // Checks user fields are not null/empty
    if (user.getFirstName() == null || user.getFirstName().isEmpty() ||
        user.getLastName() == null || user.getLastName().isEmpty() ||
        user.getHomeAddress() == null || user.getHomeAddress().isEmpty()) {
      return false;
    }
    return true;
  }
}
