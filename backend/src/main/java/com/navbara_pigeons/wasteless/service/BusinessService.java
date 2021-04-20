package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.UnhandledException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import net.minidev.json.JSONObject;

public interface BusinessService {

  JSONObject saveBusiness(Business business) throws BusinessTypeException, UserNotFoundException;

  JSONObject getBusinessById(long id, boolean includeAdmins) throws BusinessNotFoundException, UnhandledException;

}
