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
   * space/tab/etc. Ensure sign up form password regexp also updated
   *
   * @param password Users password
   */
  public static boolean isPasswordValid(String password) {
    String passwordRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";
    return Pattern.matches(passwordRegex, password);
  }

  /**
   * Uses regexp to check validity. Does not conform to RFC5322 but uses stricter version similar to that
   * used by browsers for email validation, modified to disallow dot-less domains. The same regexp is used
   * in the sign up form.
   * See https://html.spec.whatwg.org/multipage/input.html#e-mail-state-(type%3Demail) for original
   * @param email Users email
   * @return false if email doesn't pass validation
   */
  public static boolean isEmailValid(String email) {
    String emailRegex = "^[a-zA-Z0-9.!#$%&'*+\\/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)+$"; // The + at the end is a replacement for * to disallow dot-less domains
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
   *
   * @param str value string to check
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
    for (String val : new String[]{
        user.getFirstName(), user.getLastName(),
        user.getEmail(), user.getPassword(),
        user.getDateOfBirth()
    }) {
      if (UserServiceValidation.isNullOrTrimmedEmpty(val)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Returns false if required sql fields are null/empty
   *
   * @param user User
   */
  public static boolean isAddressValid(User user) {
    // Checks user fields are not null/empty
    Address address = user.getHomeAddress();
    if (UserServiceValidation.isNullOrTrimmedEmpty(address.getCountry())) {
      return false;
    }
    return true;
  }
}
