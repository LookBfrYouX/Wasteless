package com.navbara_pigeons.wasteless.security;

import com.navbara_pigeons.wasteless.security.model.BasicUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * A custom cors class
 * Used when just checking if a user is authenticated isn't enough!
 */
@Component
public class CustomCors {
  /**
   * Will return true if the authenticated user is admin of the business with parsed id
   * @param businessId the id of the business to check against
   * @return true if the user is an admin of the business
   */
  public boolean isBusinessAdmin(int businessId) {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    System.out.println("WELCOME!");

    // If user has any authentication
    if (authentication.getAuthorities().toString() != "[ROLE_ANONYMOUS]" ) {
      System.out.println( ((BasicUserDetails) authentication.getPrincipal()).getUsername() );

      // TODO: Check user is admin of business
    }
    return false;
  }
}
