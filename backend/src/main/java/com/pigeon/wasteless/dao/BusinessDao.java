package com.pigeon.wasteless.dao;

import com.pigeon.wasteless.entity.Business;
import com.pigeon.wasteless.exception.BusinessNotFoundException;

public interface BusinessDao {

  void saveBusiness(Business business);

  Business getBusinessById(long id) throws BusinessNotFoundException;

}
