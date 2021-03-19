package com.pigeon.wasteless.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.pigeon.wasteless.entity.Business;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BusinessDaoTest {

  @Autowired
  BusinessDao businessDao;

  @Test
  @Transactional
  void saveBusinessTest() {
    Business business = makeBusiness();
    businessDao.saveBusiness(business);
    assertTrue(business.getId() > 0);
  }

  @Test
  void saveValidBusinessAndGetByIdTest() {
    Business testBusiness = makeBusiness();
    businessDao.saveBusiness(testBusiness);
    Business returnedBusiness = null;
    try {
      returnedBusiness = businessDao.getBusinessById(testBusiness.getId());
    } catch (Exception exc) {
      exc.printStackTrace();
    }
    assertEquals(testBusiness.getId(), returnedBusiness.getId());
    assertEquals(testBusiness.getName(), returnedBusiness.getName());
    assertEquals(testBusiness.getBusinessType(), returnedBusiness.getBusinessType());
  }

  @Test
  void GetBusinessByInvalidIdTest() {
    assertThrows(Exception.class, () -> businessDao.getBusinessById(1000000000));
  }

  @Transactional
  void saveUserTransactionalHelper() {

  }


  Business makeBusiness() {
    Business business = new Business();
    business.setName("test")
        .setCreated("2020-07-14T14:32:00Z")
        .setAddress("some address")
        .setBusinessType("Non-profit organisation")
        .setId(0)
        .setDescription("some description");
    return business;
  }

}
