package com.navbara_pigeons.wasteless.security;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.security.model.BasicUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

/**
 * A custom cors class
 * Used when just checking if a user is authenticated isn't enough!
 */
@Component
public class AccessRules {
  private final UserDao userDao;
  private final BusinessDao businessDao;

  @Autowired
  public AccessRules(UserDao userDao, BusinessDao businessDao) {
    this.userDao = userDao;
    this.businessDao = businessDao;
  }

  /**
   * Will return true if the authenticated user is admin of the business with parsed id
   * @param businessId the id of the business to check against
   * @return true if the user is an admin of the business
   */
  public boolean isBusinessAdmin(int businessId, Authentication authentication) {
    System.out.println("WELCOME!");
    // If user has any authentication
    if (authentication.getPrincipal() != "anonymousUser" ) {
      String username = ((BasicUserDetails) authentication.getPrincipal()).getUsername();
      System.out.println(((BasicUserDetails) authentication.getPrincipal()).getUsername());

      User user;
      Business business;
      try {
        user = userDao.getUserByEmail(username);
        business = businessDao.getBusinessById(businessId);
      } catch (UserNotFoundException exc) {
        return false;
      } catch (BusinessNotFoundException exc) {
        return false;
      }
      if (business.getPrimaryAdministratorId() == user.getId()) {
        return true;
      }
      for (User admin : business.getAdministrators()) {
        if (admin.getId() == user.getId()) {
          return true;
        }
      }
    }
    return false;
  }
}