package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;

@SpringBootTest
public class BusinessServiceImplTest extends ServiceTestProvider {

  @Autowired
  BusinessServiceImpl businessService;

  @Test
  @WithUserDetails("amf133@uclive.ac.nz")
  void saveBusinessInvalidBusinessTypes() {
    // Test invalid businessType fields
    String[] businessTypeTests = {"asd", "123", "Marketing", "Retail", "Service"};
    for (String businessTypeTest : businessTypeTests) {
      Business testBusiness = makeBusiness();
      testBusiness.setBusinessType(businessTypeTest);
      assertThrows(BusinessTypeException.class, () -> businessService.saveBusiness(testBusiness));
    }
  }

  @Test
  @WithUserDetails("amf133@uclive.ac.nz")
  void saveBusinessValidBusinessTypes() {
    // Test valid businessType fields
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
  void saveBusinessInvalidAddressTest() {
    Business testBusiness = makeBusiness();
    testBusiness.getAddress().setCountry("");
    assertThrows(AddressValidationException.class, () -> businessService.saveBusiness(testBusiness));
  }

  @Test
  @WithUserDetails("amf133@uclive.ac.nz")
  void saveBusinessInvalidCountryTest() {
    Business testBusiness = makeBusiness();
    testBusiness.getAddress().setCountry("Fake Zealand");
    assertThrows(AddressValidationException.class, () -> businessService.saveBusiness(testBusiness));
  }
}
