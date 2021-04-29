package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.BusinessRegistrationException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class BusinessServiceImplTest extends ServiceTestProvider {

  @Autowired
  UserController userController;

  @Autowired
  BusinessDao businessDao;

  @Autowired
  AddressDao addressDao;

  @Autowired
  BusinessService businessService;

  @Test
  void saveBusinessInvalidBusinessTypes() {
    // Test invalid businessType fields
    loginWithCredentials();
    String[] businessTypeTests = {"asd", "123", "Marketing", "Retail", "Service"};
    for (String businessTypeTest : businessTypeTests) {
      Business testBusiness = makeBusiness();
      testBusiness.setBusinessType(businessTypeTest);
      assertThrows(BusinessTypeException.class, () -> businessService.saveBusiness(testBusiness));
    }
  }

  @Test
  void saveBusinessValidBusinessTypes() {
    // Test valid businessType fields
    loginWithCredentials();

    String[] businessTypeTests = {"Accommodation and Food Services", "Retail Trade",
        "Charitable organisation", "Non-profit organisation"};
    for (String businessTypeTest : businessTypeTests) {
      Business testBusiness = makeBusiness();
      testBusiness.setBusinessType(businessTypeTest);
      Assertions.assertDoesNotThrow(() -> businessService.saveBusiness(testBusiness));
    }
  }

  @Test void saveBusinessInvalidAddressTest() {
    loginWithCredentials();

    Business testBusiness = makeBusiness();
    testBusiness.getAddress().setCountry("");
    assertThrows(BusinessRegistrationException.class, () -> businessService.saveBusiness(testBusiness));
  }

  @Test void saveBusinessInvalidCountryTest() {
    loginWithCredentials();

    Business testBusiness = makeBusiness();
    testBusiness.getAddress().setCountry("Fake Zealand");
    assertThrows(BusinessRegistrationException.class, () -> businessService.saveBusiness(testBusiness));
  }
}
