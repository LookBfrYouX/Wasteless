package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import com.navbara_pigeons.wasteless.dto.BasicBusinessDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
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
    Assertions.assertDoesNotThrow(() -> businessService.getBusinessById(testBusiness.getId()));
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

    // Getting new business for user "dnb36@uclive.ac.nz", asserting newBusiness is BasicBusinessDto
    try {
      BasicBusinessDto newBusiness = (BasicBusinessDto) businessService.getBusinessById(testBusiness.getId());
    } catch (BusinessNotFoundException | UserNotFoundException | ClassCastException e) {
      assert(false);
    }
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
