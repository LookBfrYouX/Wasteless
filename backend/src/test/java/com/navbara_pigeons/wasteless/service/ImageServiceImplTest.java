package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.navbara_pigeons.wasteless.dao.ImageDao;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Image;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessRegistrationException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserAlreadyExistsException;
import com.navbara_pigeons.wasteless.exception.UserAuthenticationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserRegistrationException;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import java.io.FileInputStream;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.web.multipart.MultipartFile;

class ImageServiceImplTest extends ServiceTestProvider {

  MockMultipartFile testImage;
  MockMultipartFile testImage2;

  @InjectMocks
  ImageServiceImpl imageService;

  @Mock
  ImageDao imageDao;

  @Mock
  BusinessService businessService;

  @Mock
  ProductService productService;

  @Mock
  UserService userService;

  User user; // Admin user
  long USER_ID = 100;
  Business testBusiness;
  long BUSINESS_ID = 100;
  Product testProduct;
  long PRODUCT_ID = 100;

  /**
   * Set up testing environment. Create a User, Image, Business and a test Product. No other
   * businesses/products exist
   */
  @BeforeEach
  public void initialise()
      throws IOException, UserNotFoundException, AddressValidationException, BusinessTypeException,
      BusinessRegistrationException, UserAuthenticationException, UserRegistrationException, UserAlreadyExistsException, BusinessNotFoundException, ProductNotFoundException {
    // Get the test image
    FileInputStream fis = new FileInputStream("./src/test/resources/TestImage.jpeg");
    testImage = new MockMultipartFile("TestImage.jpeg", "TestImage.jpeg", "image/jpeg", fis);
    fis = new FileInputStream("./src/test/resources/TestImage.jpeg");
    testImage2 = new MockMultipartFile("TestImage2.jpeg", "TestImage2.jpeg", "image/jpeg", fis);
    // Create the test user
    user = makeUser(EMAIL_1, PASSWORD_1, false);
    user.setId(USER_ID);
    testBusiness = makeBusiness(user);
    user.addBusiness(testBusiness);
    testBusiness.setId(BUSINESS_ID);
    // Create the test product (Milk) and save the business
    testProduct = makeProduct("2L Full Cream Milk");
    testProduct.setId(PRODUCT_ID);
    testBusiness.addCatalogueProduct(testProduct);

    when(userService.isAdmin()).thenReturn(true);

    when(businessService.isBusinessAdmin(any(Long.class))).thenAnswer(new Answer<Boolean>() {
      @Override
      public Boolean answer(InvocationOnMock invocation) throws BusinessNotFoundException {
        if (BUSINESS_ID == (Long) invocation.getArgument(0)) {
          return true;
        }
        throw new BusinessNotFoundException();
      }
    });

    when(productService.getProduct(any(Long.class))).thenAnswer(new Answer<Product>() {
      @Override
      public Product answer(InvocationOnMock invocation) throws Throwable {
        if (PRODUCT_ID == (Long) invocation.getArgument(0)) {
          return testProduct;
        }
        throw new ProductNotFoundException();
      }
    });

    doNothing().when(imageDao).saveProductImageToDb(any(Image.class));
    doNothing().when(imageDao)
        .saveProductImageToMachine(any(MultipartFile.class), any(String.class));
  }

  // User making the request must be admin in order for the primary administrator to be different from signed in user
  @Test
  @WithUserDetails("mbi47@uclive.ac.nz")
  void uploadProductImage() {
    assertDoesNotThrow(() -> {
      imageService.uploadProductImage(BUSINESS_ID, PRODUCT_ID, testImage);
    });
  }

  @Test
  @WithUserDetails("mbi47@uclive.ac.nz")
  void uploadProductImageWithNonExistingBusiness() {
    assertThrows(BusinessNotFoundException.class, () -> {
      imageService.uploadProductImage(0, PRODUCT_ID, testImage);
    });
  }

  @Test
  @WithUserDetails("mbi47@uclive.ac.nz")
  void uploadProductImageWithNonExistingProduct() {
    assertThrows(ProductNotFoundException.class, () -> {
      imageService.uploadProductImage(BUSINESS_ID, 0, testImage);
    });
  }
}