package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BusinessServiceImplTest {

  @Autowired
  UserController userController;

  @Autowired
  BusinessDao businessDao;

  @Autowired
  AddressDao addressDao;

  @Autowired
  BusinessService businessService;

  @Autowired
  UserService userService;

  @Test
  void saveInvalidBusiness() {
    // Test invalid businessType fields
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setEmail("dnb36@uclive.ac.nz");
    userCredentials.setPassword("fun123");
    try {
      userService.login(userCredentials);
    } catch (Exception e) {
      e.printStackTrace();
    }

    String[] businessTypeTests = {"asd", "123", "Marketing", "Retail", "Service"};
    for (String businessTypeTest : businessTypeTests) {
      Business testBusiness = makeBusiness();
      testBusiness.setBusinessType(businessTypeTest);
      assertThrows(Exception.class, () -> businessService.saveBusiness(testBusiness));
    }
  }

  @Test
  void saveValidBusiness() {
    // Test valid businessType fields
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setEmail("dnb36@uclive.ac.nz");
    userCredentials.setPassword("fun123");
    try {
      userService.login(userCredentials);
    } catch (Exception e) {
      e.printStackTrace();
    }

    String[] businessTypeTests = {"Accommodation and Food Services", "Retail Trade",
        "Charitable organisation", "Non-profit organisation"};
    for (String businessTypeTest : businessTypeTests) {
      Business testBusiness = makeBusiness();
      testBusiness.setBusinessType(businessTypeTest);
      Assertions.assertDoesNotThrow(() -> businessService.saveBusiness(testBusiness));
    }
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
