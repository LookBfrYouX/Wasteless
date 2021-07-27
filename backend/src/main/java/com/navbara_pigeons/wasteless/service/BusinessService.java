package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import com.navbara_pigeons.wasteless.exception.BusinessAdminException;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessRegistrationException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import net.minidev.json.JSONObject;

public interface BusinessService {

  JSONObject saveBusiness(Business business)
      throws UserNotFoundException, BusinessRegistrationException, AddressValidationException;

  Business getBusiness(long id) throws BusinessNotFoundException, UserNotFoundException;

  boolean isBusinessAdmin(long businessId) throws BusinessNotFoundException, UserNotFoundException;

  void addBusinessAdmin(long businessId, long userId)
      throws UserNotFoundException, BusinessNotFoundException, InsufficientPrivilegesException;

  void removeBusinessAdmin(long businessId, long userId)
      throws UserNotFoundException, BusinessNotFoundException, InsufficientPrivilegesException, BusinessAdminException;
}
