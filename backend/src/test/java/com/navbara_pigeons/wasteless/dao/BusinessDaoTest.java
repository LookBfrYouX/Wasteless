package com.navbara_pigeons.wasteless.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import javax.transaction.Transactional;

import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BusinessDaoTest extends MainTestProvider {

  @Autowired
  BusinessDao businessDao;

  @Autowired
  AddressDao addressDao;

  @Test
  @Transactional
  void saveBusinessTest() {
    Business business = makeBusiness();
    addressDao.saveAddress(business.getAddress());
    businessDao.saveBusiness(business);
    assertTrue(business.getId() > 0);
  }

  @Test
  void saveValidBusinessAndGetByIdTest() {
    Business business = makeBusiness();
    addressDao.saveAddress(business.getAddress());
    businessDao.saveBusiness(business);
    Business returnedBusiness = null;
    try {
      returnedBusiness = businessDao.getBusinessById(business.getId());
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    assertEquals(business.getId(), returnedBusiness.getId());
    assertEquals(business.getName(), returnedBusiness.getName());
    assertEquals(business.getBusinessType(), returnedBusiness.getBusinessType());
  }

  @Test
  void GetBusinessByInvalidIdTest() {
    assertThrows(Exception.class, () -> businessDao.getBusinessById(1000000000));
  }


}
