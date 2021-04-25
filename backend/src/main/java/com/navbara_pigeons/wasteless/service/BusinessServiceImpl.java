package com.navbara_pigeons.wasteless.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.UnhandledException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.validation.BusinessServiceValidation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.transaction.Transactional;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.parser.ParseException;
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
  private final AddressDao addressDao;
  private final UserDao userDao;
  private final ObjectMapper objectMapper;
  private final UserService userService;

  /**
   * BusinessServiceImplementation constructor that takes autowired parameters and sets up the
   * service for interacting with all business related services.
   *
   * @param businessDao The BusinessDataAccessObject.
   */
  @Autowired
  public BusinessServiceImpl(BusinessDao businessDao, AddressDao addressDao, UserDao userDao,
      ObjectMapper objectMapper, @Lazy UserService userService) {
    // Using @Lazy to prevent Circular Dependencies
    this.businessDao = businessDao;
    this.addressDao = addressDao;
    this.userDao = userDao;
    this.objectMapper = objectMapper;
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
   * @param id            the id of the business
   * @param includeAdmins true if admins are to be included in the response
   * @return the Business instance of the business
   * @throws BusinessNotFoundException when business with given id does not exist
   * @throws UnhandledException        thrown when converting business to JSONObject (internal error
   *                                   500)
   */
  @Override
  public JSONObject getBusinessById(long id, boolean includeAdmins)
      throws BusinessNotFoundException, UnhandledException {
    Business business = businessDao.getBusinessById(id);

    // Convert business entity JSONObject (convert to String then to JSONObject)
    String jsonStringBusiness;
    try {
      jsonStringBusiness = objectMapper.writeValueAsString(business);
    } catch (JsonProcessingException exc) {
      throw new UnhandledException("JSON processing exception");
    }
    JSONObject response = null;
    try {
      response = (JSONObject) new JSONParser().parse(jsonStringBusiness);
    } catch (ParseException exc) {
      throw new UnhandledException("JSON parse exception");
    }

    // If business admins need to be part of the response
    if (includeAdmins) {
      // Remove sensitive information from response by calling userService
      for (int i = 0; i < ((JSONArray) response.get("administrators")).size(); i++) {
        try {
          // Using index 0 as new admins are being added to the back of the array
          // Looks complicated but its just a lot of type casting
          long adminId = Long.parseLong(
              (((JSONObject) ((JSONArray) (response.get("administrators"))).get(0)).get("id")
                  .toString()));
          ((JSONArray) (response.get("administrators"))).remove(0);
          JSONObject newAdmin = userService.getUserById(adminId, false);
          ((JSONArray) (response.get("administrators"))).appendElement(newAdmin);
        } catch (Exception exc) {
          throw new UnhandledException("User not found, server error!");
        }
      }
    } else {
      response.remove("administrators");
    }
    return response;
  }
}
