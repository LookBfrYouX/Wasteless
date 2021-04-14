package com.navbara_pigeons.wasteless.dao;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import javax.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BusinessDaoTest {

  @Autowired
  BusinessDao businessDao;

  @Autowired
  AddressDao addressDao;

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

  Address makeAddress() {
    Address address = new Address();
    address.setStreetNumber("3/24")
        .setStreetName("Ilam Road")
        .setPostcode("90210")
        .setCity("Christchurch")
        .setRegion("Canterbury")
        .setCountry("New Zealand");
    addressDao.saveAddress(address);
    return address;
  }

  Business makeBusiness() {
    Business business = new Business();
    business.setName("test")
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
        .setBusinessType("Non-profit organisation")
        .setAddress(makeAddress())
        .setId(0)
        .setDescription("some description");
    return business;
  }

}
