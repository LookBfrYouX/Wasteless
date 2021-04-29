package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.security.model.BasicUserDetails;
import com.navbara_pigeons.wasteless.validation.AddressValidator;
import com.navbara_pigeons.wasteless.validation.BusinessServiceValidation;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import javax.transaction.Transactional;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * A UserService implementation.
 */
@Service
public class BusinessServiceImpl implements BusinessService {

  private final BusinessDao businessDao;

  private final AddressDao addressDao;

  private final UserDao userDao;

  private final UserService userService;

  private final AddressValidator addressValidator;
  /**
   * BusinessServiceImplementation constructor that takes autowired parameters and sets up the
   * service for interacting with all business related services.
   *
   * @param businessDao The BusinessDataAccessObject.
   */
  @Autowired
  public BusinessServiceImpl(BusinessDao businessDao, AddressDao addressDao, UserDao userDao, AddressValidator addressValidator, UserService userService) {
    this.businessDao = businessDao;
    this.addressDao = addressDao;
    this.userDao = userDao;
    this.addressValidator = addressValidator;
    this.userService = userService;
  }

  /**
   * Performs basic business checks, sets role, created date and hashes password before sending to
   * the dao
   *
   * @param business Business object to be saved.
   * @throws BusinessTypeException Thrown when a businessType is not an authorised businessType
   */
  @Override
  @Transactional
  public JSONObject saveBusiness(Business business)
          throws BusinessTypeException, UserNotFoundException, BusinessRegistrationException {
    if (!BusinessServiceValidation.isBusinessTypeValid(business.getBusinessType())) {
      throw new BusinessTypeException("Invalid BusinessType");
    }

    // Address validation
    if (!addressValidator.requiredFieldsNotEmpty(business.getAddress())) {
      throw new BusinessRegistrationException("Required address fields cannot be null");
    }

    Boolean countryValid = addressValidator.isCountryValid(business.getAddress().getCountry());
    if (countryValid == null) {
      // TODO change this to a 500 error instead
      throw new BusinessRegistrationException("Could not fetch list of countries for validation");
    } else if (!countryValid.booleanValue()) {
      throw new BusinessRegistrationException("Country does not exist is is not known");
    }


    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    User currentUser = this.userDao.getUserByEmail(authentication.getName());
    business.addAdministrator(currentUser);
    business.setCreated(ZonedDateTime.now(ZoneOffset.UTC));
    this.addressDao.saveAddress(business.getAddress());
    this.businessDao.saveBusiness(business);
    JSONObject response = new JSONObject();
    response.put("businessId", business.getId());
    return response;
  }

  /**
   * Calls the businessDao to get the specified business
   *
   * @param id the id of the business
   * @return the Business instance of the business
   */
  @Override
  public JSONObject getBusinessById(long id)
      throws BusinessNotFoundException, UserNotFoundException {
    Business business = this.businessDao.getBusinessById(id);

    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    User user = this.userDao.getUserByEmail(authentication.getName());
    JSONObject response = new JSONObject();
    response.put("id", id);
    response.put("name", business.getName());
    response.put("description", business.getDescription());
    response.put("businessType", business.getBusinessType());
    response.put("created", business.getCreated());

    JSONObject address = new JSONObject();
    response.put("homeAddress", address);
    response.put("primaryAdministratorId", business.getPrimaryAdministratorId());

    // Email of user that made the request
    String username = ((BasicUserDetails) authentication.getPrincipal()).getUsername();
    boolean isAdmin = false;
    for (GrantedAuthority simpleGrantedAuthority : authentication.getAuthorities()) {
      if (simpleGrantedAuthority.getAuthority().contains("ADMIN")) {
        isAdmin = true;
      }
    }
    boolean isAdministrator = false;
    for (User administrator : business.getAdministrators()) {
      if (administrator.getEmail() == username) {
        isAdministrator = true;
      }
    }
    // sensitive details (e.g. email, postcode) are not returned
    if (business.getPrimaryAdministratorId() == user.getId() || isAdministrator || isAdmin) {
      address.put("streetNumber", user.getHomeAddress().getStreetNumber());
      address.put("streetName", user.getHomeAddress().getStreetName());
      address.put("postcode", user.getHomeAddress().getPostcode());
    }

    address.put("city", user.getHomeAddress().getCity());
    address.put("region", user.getHomeAddress().getRegion());
    address.put("country", user.getHomeAddress().getCountry());
    return response;
  }

  /**
   * This helper method tests if the currently logged in user is an administrator of the business with the given ID.
   * @param businessId The business to test against.
   * @return True if the current user is the primary admin or a regular admin
   * @throws BusinessNotFoundException The business does not exist
   * @throws UserNotFoundException The user does not exist
   */
  public boolean isBusinessAdmin(long businessId) throws BusinessNotFoundException, UserNotFoundException {
    Business business = this.businessDao.getBusinessById(businessId);
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    User authUser = this.userService.getUserByEmail(auth.getName());
    if (business.getPrimaryAdministratorId() == authUser.getId()) {
      return true;
    }
    for (User user : business.getAdministrators()) {
      if (authUser.getId() == user.getId()) {
        return true;
      }
    }
    return false;
  }

}
