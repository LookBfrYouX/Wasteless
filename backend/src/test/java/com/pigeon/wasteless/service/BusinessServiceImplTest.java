package com.pigeon.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.pigeon.wasteless.controller.UserController;
import com.pigeon.wasteless.dao.BusinessDao;
import com.pigeon.wasteless.entity.Business;
import com.pigeon.wasteless.security.model.UserCredentials;
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
  BusinessService businessService;

  @Autowired
  UserService userService;

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  void saveInvalidBusiness() {
    // Test invalid businessType fields
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

    String[] businessTypeTests = {"Accommodation and Food Services", "Retail Trade", "Charitable organisation", "Non-profit organisation"};
    for (String businessTypeTest : businessTypeTests) {
      Business testBusiness = makeBusiness();
      testBusiness.setBusinessType(businessTypeTest);
      assertDoesNotThrow(() -> businessService.saveBusiness(testBusiness));
    }
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
