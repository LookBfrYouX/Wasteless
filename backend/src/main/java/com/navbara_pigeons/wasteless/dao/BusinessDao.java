package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;

public interface BusinessDao {

  void saveBusiness(Business business);

  Business getBusinessById(long id) throws BusinessNotFoundException;

}
