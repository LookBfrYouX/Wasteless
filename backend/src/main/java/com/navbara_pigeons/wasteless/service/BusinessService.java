package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.*;
import net.minidev.json.JSONObject;

public interface BusinessService {

  JSONObject saveBusiness(Business business) throws BusinessTypeException, UserNotFoundException, BusinessRegistrationException, AddressValidationException;

  JSONObject getBusinessById(long id) throws BusinessNotFoundException, UserNotFoundException;

  boolean isBusinessAdmin(long businessId) throws BusinessNotFoundException, UserNotFoundException;

}
