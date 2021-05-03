package com.navbara_pigeons.wasteless.service;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

@SpringBootTest
public class BusinessServiceImplTest extends ServiceTestProvider {
  
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

  @Test
  @WithUserDetails("amf133@uclive.ac.nz")
  void getBusinessByIdExpectOk() {
    Business testBusiness = makeBusiness();
    Assertions.assertDoesNotThrow(() -> businessService.saveBusiness(testBusiness));
    Assertions.assertDoesNotThrow(() -> businessService.getBusinessById(testBusiness.getId(), true));
  }

  @Test
  @WithUserDetails("amf133@uclive.ac.nz")
  void getBusinessByIdExpectAdminsHidden() {
    Business testBusiness = makeBusiness();
    Assertions.assertDoesNotThrow(() -> businessService.saveBusiness(testBusiness));
    JSONObject newBusiness = null;
    try {
      // includeAdmins set to false, assuming no admins are returned in response
      newBusiness = businessService.getBusinessById(testBusiness.getId(), false);
    } catch (BusinessNotFoundException e) {
      assert(false);
    } catch (UnhandledException e) {
      assert(false);
    }
    assertFalse(newBusiness.containsKey("administrators"));
  }

  @Test
  @WithUserDetails("amf133@uclive.ac.nz")
  void getBusinessByIdCheckSensitiveFieldsHidden() {
    // Check sensitive fields are not shown to "dnb36@uclive.ac.nz" (not admin or GAA)
    Business testBusiness = makeBusiness();
    Assertions.assertDoesNotThrow(() -> businessService.saveBusiness(testBusiness));

    // Logging in as "dnb36@uclive.ac.nz"
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setEmail("dnb36@uclive.ac.nz");
    userCredentials.setPassword("fun123");
    Assertions.assertDoesNotThrow(() -> userService.login(userCredentials));

    // Getting new business for user "dnb36@uclive.ac.nz"
    JSONObject newBusiness = null;
    try {
      newBusiness = businessService.getBusinessById(testBusiness.getId(), true);
    } catch (BusinessNotFoundException e) {
      assert(false);
    } catch (UnhandledException e) {
      assert(false);
    }

    // Administrators should only have one admin "amf133@uclive.ac.nz"
    assertFalse(newBusiness.get("administrators").toString().contains("\"password\""));
    assertFalse(newBusiness.get("administrators").toString().contains("\"email\""));
    assertFalse(newBusiness.get("administrators").toString().contains("\"dateOfBirth\""));
    assertFalse(newBusiness.get("administrators").toString().contains("\"phoneNumber\""));
    assertFalse(newBusiness.get("administrators").toString().contains("\"streetNumber\""));
    assertFalse(newBusiness.get("administrators").toString().contains("\"streetName\""));
    assertFalse(newBusiness.get("administrators").toString().contains("\"postcode\""));
  }


  @Test void saveBusinessInvalidAddressTest() {
    loginWithCredentials();

    Business testBusiness = makeBusiness();
    testBusiness.getAddress().setCountry("");
    assertThrows(AddressValidationException.class, () -> businessService.saveBusiness(testBusiness));
  }

  @Test
  void saveBusinessInvalidCountryTest() {
    loginWithCredentials();

    Business testBusiness = makeBusiness();
    testBusiness.getAddress().setCountry("Fake Zealand");
    assertThrows(AddressValidationException.class, () -> businessService.saveBusiness(testBusiness));
  }
}
