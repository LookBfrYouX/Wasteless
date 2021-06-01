package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessRegistrationException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;

class ImageServiceImplTest extends ServiceTestProvider {

  MockMultipartFile testImage;
  MockMultipartFile testImage2;

  Business testBusiness;

  Product testProduct;

  @Autowired
  ImageService imageService;

  /**
   * Set up testing environment. Create a User, Image, Business and a test Product.
   */
  @BeforeEach
  public void initialise()
      throws IOException, UserNotFoundException, AddressValidationException, BusinessTypeException,
      BusinessRegistrationException {
    // Get the test image
    FileInputStream fis = new FileInputStream("./src/test/resources/TestImage.jpeg");
    testImage = new MockMultipartFile("TestImage.jpeg", "TestImage.jpeg", "image/jpeg", fis);
    fis = new FileInputStream("./src/test/resources/TestImage.jpeg");
    testImage2 = new MockMultipartFile("TestImage2.jpeg", "TestImage2.jpeg", "image/jpeg", fis);
    // Create the test user
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    testBusiness = makeBusiness();
    user.setId(123);
    user.addBusiness(testBusiness);
    // Create the test product (Milk) and save the business
    testProduct = makeProduct("2L Full Cream Milk");
    testBusiness.addCatalogueProduct(testProduct);
    businessService.saveBusiness(testBusiness);
  }

  @Test
  @WithUserDetails("fdi19@uclive.ac.nz")
  void uploadProductImage() {
    assertDoesNotThrow(() -> {
      imageService.uploadProductImage(testBusiness.getId(), testProduct.getId(), testImage);
    });
  }

  @Test
  @WithUserDetails("fdi19@uclive.ac.nz")
  void uploadProductImageWithNonExistingBusiness() {
    assertThrows(BusinessNotFoundException.class, () -> {
      imageService.uploadProductImage(0, testProduct.getId(), testImage);
    });
  }

  @Test
  @WithUserDetails("fdi19@uclive.ac.nz")
  void uploadProductImageWithNonExistingProduct() {
    assertThrows(ProductNotFoundException.class, () -> {
      imageService.uploadProductImage(testBusiness.getId(), 0, testImage);
    });
  }
}