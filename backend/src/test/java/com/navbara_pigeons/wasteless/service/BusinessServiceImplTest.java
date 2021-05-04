package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dto.BasicBusinessDto;
import com.navbara_pigeons.wasteless.dto.FullBusinessDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

@SpringBootTest
public class BusinessServiceImplTest extends ServiceTestProvider {

  @Mock
  BusinessDao businessDao;

  @Mock
  AddressService addressService;

  @Mock
  UserService userService;

  @InjectMocks
  BusinessServiceImpl businessService;

  void mockLoggedInUser(User user) throws UserNotFoundException {
    when(userService.getLoggedInUser()).thenReturn(user);
  }

  @Test
  @WithMockUser()
  void getBusiness_onePrimaryAdmin() throws BusinessNotFoundException, UserNotFoundException {
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    Business business = makeBusiness(BUSINESS_1_NAME, user);
    user.addBusiness(business);
    mockLoggedInUser(user);
    when(businessDao.getBusinessById(business.getId())).thenReturn(business);
    Object businessDto = businessService.getBusinessById(business.getId());

    assertBusinessEquals(business, (FullBusinessDto) businessDto);
  }

  @Test
  @WithMockUser()
  void getBusiness_onePrimaryAdminOneProduct() throws BusinessNotFoundException, UserNotFoundException {
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    Business business = makeBusiness(BUSINESS_1_NAME, user);
    user.addBusiness(business);
    mockLoggedInUser(user);
    when(businessDao.getBusinessById(business.getId())).thenReturn(business);
    Object businessDto = businessService.getBusinessById(business.getId());

    assertBusinessEquals(business, (FullBusinessDto) businessDto);
  }

  @Test
  @Disabled
  @WithMockUser()
  void getBusiness_twoAdmins() throws BusinessNotFoundException, UserNotFoundException {
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    user.setId(100);
    User user2 = makeUser(EMAIL_2, PASSWORD_1, false);
    user.setId(101);
    Business business = makeBusiness(BUSINESS_1_NAME, user);
    user.addBusiness(business);
    user2.addBusiness(business);
    business.getAdministrators().add(user2);

    mockLoggedInUser(user);
    when(businessDao.getBusinessById(business.getId())).thenReturn(business);
    Object businessDto = businessService.getBusinessById(business.getId());

    assertBusinessEquals(business, (FullBusinessDto) businessDto);
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void saveBusiness_normal() throws UserNotFoundException, AddressValidationException, BusinessTypeException {
    Business business = makeBusiness();
    businessService.saveBusiness(business);
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void saveBusiness_badType() {
    String[] testValues = {"asd", "123", "Marketing", "Retail", "Service"};
    Business business = makeBusiness();
    for(String testValue: testValues) {
      business.setBusinessType(testValue);
    }
    assertThrows(BusinessTypeException.class, () -> businessService.saveBusiness(business));
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
}
