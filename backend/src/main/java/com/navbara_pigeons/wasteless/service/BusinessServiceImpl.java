package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dto.BasicBusinessDto;
import com.navbara_pigeons.wasteless.dto.FullBusinessDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.validation.BusinessServiceValidation;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import javax.transaction.Transactional;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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


  @Value("${public_path_prefix}")
  private String publicPathPrefix;


  /**
   * BusinessServiceImplementation constructor that takes autowired parameters and sets up the
   * service for interacting with all business related services.
   *
   * @param businessDao The BusinessDataAccessObject.
   */
  @Autowired
  public BusinessServiceImpl(BusinessDao businessDao, AddressService addressService,
      @Lazy UserService userService) {
    // Using @Lazy to prevent Circular Dependencies
    this.businessDao = businessDao;
    this.addressService = addressService;
    this.userService = userService;
  }


  /**
   * Performs basic business checks. If the primary administrator is not in the list of administrators, they are fetched
   * and added. If the primary administrator does not have the business in the list of businesses administered, the
   * business is added and the user is saved
   *
   * @param business Business object to be saved.
   * @throws BusinessTypeException Thrown when a businessType is not an authorised businessType
   */
  @Override
  @Transactional
  public JSONObject saveBusiness(Business business)
          throws BusinessRegistrationException, UserNotFoundException, AddressValidationException {
    User currentUser = this.userService.getLoggedInUser();

    // When create business request is made, administrators cannot be set (either by user or in the Business(CreateBusinessDto) constructor
    // Hence, check if the primary administrator is already in the list of administrators and if not, add them
    User primaryAdministrator = null;
    if (business.getPrimaryAdministratorId() == null) {
      // Default to current user if not given
      business.setPrimaryAdministratorId(currentUser.getId());
    }

    for(User admin: business.getAdministrators()) {
      if (admin.getId() == business.getPrimaryAdministratorId()) {
        primaryAdministrator = admin;
        break;
      }
    }

    if (primaryAdministrator == null) {
      primaryAdministrator = userService.getUserById(business.getPrimaryAdministratorId());
      business.addAdministrator(primaryAdministrator);
    }

    if (currentUser.getId() != primaryAdministrator.getId() && !this.userService.isAdmin()) {
      throw new BusinessRegistrationException("Only a GAA can create a business with someone else as the primary business administrator");
    }

    business.setCreated(ZonedDateTime.now(ZoneOffset.UTC));

    BusinessServiceValidation.validate(business, LocalDate.now());
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
   * @return a business DTO
   * @throws BusinessNotFoundException when business with given id does not exist
   * @throws UserNotFoundException     should never be thrown. If is thrown, return 500 status code
   */
  @Override
  @Transactional
  public Object getBusinessById(long id) throws BusinessNotFoundException, UserNotFoundException {
    Business business = businessDao.getBusinessById(id);

    if (isBusinessAdmin(id) || userService.isAdmin()) {
      return new FullBusinessDto(business, publicPathPrefix);
    } else {
      return new BasicBusinessDto(business);
    }
  }

  /**
   * Calls the businessDao to get the specified business
   *
   * @param id the id of the business
   * @return a business
   * @throws BusinessNotFoundException when business with given id does not exist
   */
  @Override
  @Transactional
  public Business getBusiness(long id) throws BusinessNotFoundException {
    return businessDao.getBusinessById(id);
  }

  /**
   * Adds user with given ID to list of business admins
   *
   * @param userId     of the user that will be added to the list of business admins
   * @param businessId of the business to add the admin to
   */
  @Override
  @Transactional
  public void addBusinessAdmin(long businessId, long userId)
      throws UserNotFoundException, BusinessNotFoundException, InsufficientPrivilegesException {
    User user = userService.getUserById(userId);
    Business business = getBusiness(businessId);
    if (!isBusinessPrimaryAdmin(businessId) && !userService.isAdmin()) {
      throw new InsufficientPrivilegesException("Must be the primary business admin to use this feature!");
    }
    business.addAdministrator(user);
    businessDao.saveBusiness(business);
  }

  /**
   * This helper method tests if the currently logged in user is an administrator of the business
   * with the given ID
   *
   * @param businessId The business to test against.
   * @return True if the current user is the primary admin or a regular admin
   * @throws BusinessNotFoundException The business does not exist
   * @throws UserNotFoundException     The user does not exist
   */
  public boolean isBusinessAdmin(long businessId)
      throws BusinessNotFoundException, UserNotFoundException {
    Business business = this.businessDao.getBusinessById(businessId);
    User authUser = this.userService.getLoggedInUser();

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

  /**
   * This helper method tests if the currently logged in user is the primary administrator of the
   * business with the given ID
   *
   * @param businessId The business to test against.
   * @return True if the current user is the primary admin
   * @throws BusinessNotFoundException The business does not exist
   * @throws UserNotFoundException     The user does not exist
   */
  private boolean isBusinessPrimaryAdmin(long businessId)
      throws BusinessNotFoundException, UserNotFoundException {
    Business business = this.businessDao.getBusinessById(businessId);
    User authUser = this.userService.getLoggedInUser();

    if (business.getPrimaryAdministratorId() == authUser.getId()) {
      return true;
    }
    return false;
  }
}
