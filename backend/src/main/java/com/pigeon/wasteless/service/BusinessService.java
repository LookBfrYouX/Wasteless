package com.pigeon.wasteless.service;

import com.pigeon.wasteless.entity.Business;
import com.pigeon.wasteless.exception.BusinessNotFoundException;
import com.pigeon.wasteless.exception.BusinessTypeException;
import com.pigeon.wasteless.exception.UserNotFoundException;
import net.minidev.json.JSONObject;

public interface BusinessService {

  JSONObject saveBusiness(Business business) throws BusinessTypeException, UserNotFoundException;

  Business getBusinessById(long id) throws BusinessNotFoundException;

}
