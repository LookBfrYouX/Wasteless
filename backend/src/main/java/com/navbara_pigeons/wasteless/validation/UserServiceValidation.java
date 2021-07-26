package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.User;
import java.time.format.DateTimeFormatter;
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
    String authRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+).{8,}";
    return Pattern.matches(authRegex, password);
  }

}
