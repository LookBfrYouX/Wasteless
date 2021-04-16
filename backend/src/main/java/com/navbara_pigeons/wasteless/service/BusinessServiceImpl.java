package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.security.model.BasicUserDetails;
import com.navbara_pigeons.wasteless.validation.BusinessServiceValidation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
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

  /**
   * BusinessServiceImplementation constructor that takes autowired parameters and sets up the
   * service for interacting with all business related services.
   *
   * @param businessDao The BusinessDataAccessObject.
   */
  @Autowired
  public BusinessServiceImpl(BusinessDao businessDao, AddressDao addressDao, UserDao userDao) {
    this.businessDao = businessDao;
    this.addressDao = addressDao;
    this.userDao = userDao;
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
      throws BusinessTypeException, UserNotFoundException {
    if (!BusinessServiceValidation.isBusinessTypeValid(business.getBusinessType())) {
      throw new BusinessTypeException("Invalid BusinessType");
    }
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    User currentUser = this.userDao.getUserByEmail(authentication.getName());
    business.addAdministrator(currentUser);
    business.setCreated(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME));
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
    User loggedIn = this.userDao.getUserByEmail(authentication.getName());
    JSONObject response = new JSONObject();
    response.put("id", id);
    response.put("name", business.getName());
    response.put("description", business.getDescription());
    response.put("businessType", business.getBusinessType());
    response.put("created", business.getCreated());

    JSONObject address = new JSONObject();
    response.put("address", address);
    response.put("primaryAdministratorId", business.getPrimaryAdministratorId());
    List<JSONObject> administrators = new ArrayList<>();
    if (business.getAdministrators() != null) {
      for (User user : business.getAdministrators()) {
        JSONObject administrator = new JSONObject();
        administrator.put("id", user.getId());
        administrator.put("firstName", user.getFirstName());
        administrator.put("lastName", user.getLastName());
        administrator.put("middleName", user.getMiddleName());
        administrator.put("nickname", user.getNickname());
        administrator.put("bio", user.getBio());
        administrator.put("created", user.getCreated());
        administrator.put("role", user.getRole());
        JSONObject homeAddress = new JSONObject();
        homeAddress.put("city", user.getHomeAddress().getCity());
        homeAddress.put("region", user.getHomeAddress().getRegion());
        homeAddress.put("country", user.getHomeAddress().getCountry());
        administrator.put("homeAddress", homeAddress);

        List<JSONObject> businesses = new ArrayList<>();
        for (Business businessI : user.getBusinesses()) {
          JSONObject businessAdministrated = new JSONObject();
          businessAdministrated.put("id", businessI.getId());
          businesses.add(businessAdministrated);
        }
        administrator.put("businessesAdministered", businesses);

        if (user == loggedIn) {
          homeAddress.put("streetNumber", user.getHomeAddress().getStreetNumber());
          homeAddress.put("streetName", user.getHomeAddress().getStreetName());
          homeAddress.put("postcode", user.getHomeAddress().getPostcode());
          administrator.put("email", user.getEmail());
          administrator.put("dateOfBirth", user.getDateOfBirth());
          administrator.put("phoneNumber", user.getPhoneNumber());
        }
        administrators.add(administrator);
      }
    }
    response.put("administrators", administrators);

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
    if (business.getPrimaryAdministratorId() == loggedIn.getId() || isAdministrator || isAdmin) {
      address.put("streetNumber", business.getAddress().getStreetNumber());
      address.put("streetName", business.getAddress().getStreetName());
      address.put("postcode", business.getAddress().getPostcode());
    }

    address.put("city", business.getAddress().getCity());
    address.put("region", business.getAddress().getRegion());
    address.put("country", business.getAddress().getCountry());
    return response;
  }

}
