package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.validation.BusinessServiceValidation;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.transaction.Transactional;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

  private final UserDao userDao;

  /**
   * BusinessServiceImplementation constructor that takes autowired parameters and sets up the
   * service for interacting with all business related services.
   *
   * @param businessDao The BusinessDataAccessObject.
   */
  @Autowired
  public BusinessServiceImpl(BusinessDao businessDao, UserDao userDao) {
    this.businessDao = businessDao;
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
  public Business getBusinessById(long id) throws BusinessNotFoundException {
    return this.businessDao.getBusinessById(id);
  }

}
