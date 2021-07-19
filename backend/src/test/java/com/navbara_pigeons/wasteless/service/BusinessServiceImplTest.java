package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dto.BasicBusinessDto;
import com.navbara_pigeons.wasteless.dto.FullBusinessDto;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

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
  protected long USERID_1 = 100;
  protected long USERID_2 = 101;
  protected long BUSINESSID_1 = 100;

  @BeforeEach
  void beforeAll()
      throws UserNotFoundException, AddressValidationException, BusinessNotFoundException {
    // Setting mocks before tests are run to ensure unit testing only
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    user.setId(USERID_1);
    Business business = makeBusiness();
    business.setId(BUSINESSID_1);
    business.setPrimaryAdministratorId(USERID_1);
    FullBusinessDto fullBusinessDto = new FullBusinessDto(business, "");

    when(userService.getLoggedInUser()).thenReturn(user);
    when(userService.getUserByEmail(EMAIL_1)).thenReturn(user);
    doNothing().when(businessDao).saveBusiness(any(Business.class));
    when(businessDao.getBusinessById(BUSINESSID_1)).thenReturn(business);
    doNothing().when(addressService).saveAddress(any(Address.class));
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void getBusinessByIdExpectOk()
      throws BusinessNotFoundException, UserNotFoundException, AddressValidationException, BusinessRegistrationException {
    // Checking that the FullBusinessDto is returned
    Business business = makeBusiness();
    business.setId(BUSINESSID_1);
    businessService.saveBusiness(business);
    Object businessDto = businessService.getBusinessById(business.getId());

    assertThrows(ClassCastException.class, () -> {
      BasicBusinessDto basicBusinessDto = (BasicBusinessDto) businessDto;
    });
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  void getBusiness_twoAdmins()
      throws BusinessNotFoundException, UserNotFoundException, AddressValidationException, BusinessRegistrationException {
    // Business should have 2 admins, one as primary admin, other in the list of business admins
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    user.setId(USERID_1);
    User user2 = makeUser(EMAIL_2, PASSWORD_1, false);
    user2.setId(USERID_2);
    Business business = makeBusiness();
    business.getAdministrators().add(user2);
    businessService.saveBusiness(business);

    when(businessDao.getBusinessById(business.getId())).thenReturn(business);
    Object businessDto = businessService.getBusinessById(business.getId());
    assertBusinessEquals(business, (FullBusinessDto) businessDto);
  }

//  @Test
//  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
//  void saveBusinessInvalidBusinessTypes() {
//    // Checking saveBusiness throws errors with bad business types
//    String[] testValues = {"asd", "123", "Marketing", "Retail", "Service"};
//    Business business = makeBusiness();
//    for (String testValue : testValues) {
//      business.setBusinessType(testValue);
//      assertThrows(BusinessTypeException.class, () -> businessService.saveBusiness(business));
//    }
//  }

//  @Test
//  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
//  void saveBusinessValidBusinessTypes() {
//    // Checking saveBusiness does not throw errors with good business types
//    String[] testValues = {"Accommodation and Food Services", "Retail Trade",
//        "Charitable organisation", "Non-profit organisation"
//    };
//    Business business = makeBusiness();
//    for (String testValue : testValues) {
//      business.setBusinessType(testValue);
//      Assertions.assertDoesNotThrow(() -> businessService.saveBusiness(business));
//    }
//  }

  @Test
  void makeUserBusinessAdmin_expectOk() throws Exception {
    Business business = makeBusiness();
    business.setId(BUSINESSID_1);
    business.setPrimaryAdministratorId(USERID_1);

    User user2 = makeUser(EMAIL_2, PASSWORD_1, false);
    user2.setId(USERID_2);
    when(userService.getUserById(USERID_2)).thenReturn(user2);

    Assertions.assertDoesNotThrow(() -> businessService.addBusinessAdmin(business.getId(), USERID_2));
  }

  @Test
  void makeUserBusinessAdmin_expectInsufficientPrivileges() throws Exception {
    // User 2 trying to make himself admin
    Business business = makeBusiness();
    business.setId(BUSINESSID_1);
    business.setPrimaryAdministratorId(USERID_1);

    User user2 = makeUser(EMAIL_2, PASSWORD_1, false);
    user2.setId(USERID_2);
    when(userService.getUserById(USERID_2)).thenReturn(user2);

    when(userService.getLoggedInUser()).thenReturn(user2);
    Assertions.assertThrows(InsufficientPrivilegesException.class, () -> businessService.addBusinessAdmin(business.getId(), USERID_2));
  }
}