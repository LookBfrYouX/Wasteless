package com.navbara_pigeons.wasteless.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.validation.BusinessServiceValidation;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import javax.transaction.Transactional;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * A UserService implementation.
 */
@Service
public class BusinessServiceImpl implements BusinessService {

  private final BusinessDao businessDao;
  private final AddressService addressService;
  private final UserService userService;

  /**
   * BusinessServiceImplementation constructor that takes autowired parameters and sets up the
   * service for interacting with all business related services.
   *
   * @param businessDao The BusinessDataAccessObject.
   */
  @Autowired
  public BusinessServiceImpl(BusinessDao businessDao, AddressService addressService, UserService userService) {
    // Using @Lazy to prevent Circular Dependencies
    this.businessDao = businessDao;
    this.addressService = addressService;
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
          throws BusinessTypeException, UserNotFoundException, BusinessRegistrationException, AddressValidationException {
    if (!BusinessServiceValidation.isBusinessTypeValid(business.getBusinessType())) {
      throw new BusinessTypeException("Invalid BusinessType");
    }

    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    User currentUser = this.userService.getUserByEmail(authentication.getName());
    business.addAdministrator(currentUser);
    business.setCreated(ZonedDateTime.now(ZoneOffset.UTC));

    this.addressService.saveAddress(business.getAddress());
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
   * @throws BusinessNotFoundException when business with given id does not exist
   * @throws UnhandledException thrown when converting business to JSONObject (internal error 500)
   */
  @Override
  public Business getBusinessById(long id) throws BusinessNotFoundException {
    Business business = businessDao.getBusinessById(id);
    return business;
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
