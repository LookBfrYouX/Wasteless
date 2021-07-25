package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.*;
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

import java.time.LocalDate;

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
  protected User user1; // EMAIL_1, PASSWORD_1, not admin

  /**
   * Create user1, set them as logged in user and make getUserById/Email return the user
   * Make saveAddress do nothing
   * @throws UserNotFoundException
   * @throws AddressValidationException
   */
  @BeforeEach
  void beforeAll() throws UserNotFoundException, AddressValidationException {
    // Setting mocks before tests are run to ensure unit testing only
    user1 = makeUser(EMAIL_1, PASSWORD_1, false);
    user1.setId(USERID_1);

    when(userService.getLoggedInUser()).thenReturn(user1);
    when(userService.getUserById(USERID_1)).thenReturn(user1);
    when(userService.getUserByEmail(EMAIL_1)).thenReturn(user1);
    doNothing().when(addressService).saveAddress(any(Address.class));
  }

  @Test
  void getBusinessById_admin_getFullDto() throws BusinessNotFoundException, UserNotFoundException {
    // Checking that the FullBusinessDto is returned
    User user = makeUser(EMAIL_2, PASSWORD_1, false);
    Business business = makeBusiness(user);
    business.setId(BUSINESSID_1);

    when(userService.isAdmin()).thenReturn(true);

    when(businessDao.getBusinessById(BUSINESSID_1)).thenReturn(business);
    Object businessDto = businessService.getBusinessById(business.getId());

    assertTrue(businessDto instanceof FullBusinessDto);
  }

  @Test
  void getBusinessById_businessAdmin_getFullDto() throws BusinessNotFoundException, UserNotFoundException {
    // Checking that the FullBusinessDto is returned
    Business business = makeBusiness(user1);
    business.setId(BUSINESSID_1);

    when(userService.isAdmin()).thenReturn(false);

    when(businessDao.getBusinessById(BUSINESSID_1)).thenReturn(business);
    Object businessDto = businessService.getBusinessById(business.getId());

    assertTrue(businessDto instanceof FullBusinessDto);
  }

  @Test
  void getBusinessById_notBusinessAdmin_getBasicDto() throws BusinessNotFoundException, UserNotFoundException {
    // Checking that the BasicBusinessDto is returned
    User user2 = makeUser(EMAIL_2, PASSWORD_1, false);
    user2.setId(USERID_2);

    Business business = makeBusiness(user2);
    business.setId(BUSINESSID_1);

    when(userService.isAdmin()).thenReturn(false);

    when(businessDao.getBusinessById(BUSINESSID_1)).thenReturn(business);
    Object businessDto = businessService.getBusinessById(business.getId());

    assertTrue(businessDto instanceof BasicBusinessDto);
  }

  @Test
  void createBusiness_primaryAdministratorsListNotSet()
          throws UserNotFoundException, AddressValidationException, BusinessRegistrationException {

    Business business = makeBusiness();
    business.setId(BUSINESSID_1);
    business.setPrimaryAdministratorId(user1.getId());

    when(userService.isAdmin()).thenReturn(true);
    businessService.saveBusiness(business);

    assertEquals(1, business.getAdministrators().size());
    assertEquals(USERID_1, business.getAdministrators().get(0).getId());
  }

  @Test
  void createBusiness_notAdmin_noErrors() throws UserNotFoundException, AddressValidationException, BusinessRegistrationException {
    Business business = makeBusiness(user1);
    business.setId(BUSINESSID_1);

    businessService.saveBusiness(business);
  }

  /**
   * Should not complain if primary administrator is not set
   * @throws UserNotFoundException
   * @throws AddressValidationException
   * @throws BusinessRegistrationException
   */
  @Test
  void createBusiness_notAdmin_primaryAdministratorNotSet() throws UserNotFoundException, AddressValidationException, BusinessRegistrationException {
    Business business = makeBusiness("businessName");

    businessService.saveBusiness(business);
  }
  /**
   * Non-admin user trying to create business with another user as the primary admin
   */
  @Test
  void createBusiness_notAdminOtherAsPrimary() {
    User user2 = makeUser(EMAIL_2, PASSWORD_1, false);
    user2.setId(USERID_2);

    Business business = makeBusiness(user2);
    business.setId(BUSINESSID_1);

    assertThrows(BusinessRegistrationException.class, () -> businessService.saveBusiness(business));
  }


  /**
   * Crate a business with another user as the primary admin; should succeed as user making request is admin
   */
  @Test
  void createBusiness_AdminOtherAsPrimary() throws UserNotFoundException, AddressValidationException, BusinessRegistrationException {
    User user2 = makeUser(EMAIL_2, PASSWORD_1, false);
    user2.setId(USERID_2);

    Business business = makeBusiness(user2);
    business.setId(BUSINESSID_1);

    when(userService.isAdmin()).thenReturn(true);
    businessService.saveBusiness(business);
  }

  /**
   * Crate a business but too young
   */
  @Test
  void createBusiness_primaryAdminAgeRestriction() {
    user1.setDateOfBirth(LocalDate.now().minusYears(4));
    Business business = makeBusiness(user1);
    business.setId(BUSINESSID_1);

    when(userService.isAdmin()).thenReturn(true);
    assertThrows(BusinessRegistrationException.class, () -> businessService.saveBusiness(business));
  }

  @Test
  void makeUserBusinessAdmin_expectOk() throws Exception {
    Business business = makeBusiness();
    business.setId(BUSINESSID_1);
    business.setPrimaryAdministratorId(USERID_1);

    User user2 = makeUser(EMAIL_2, PASSWORD_1, false);
    user2.setId(USERID_2);

    when(userService.getUserById(USERID_2)).thenReturn(user2);
    when(businessDao.getBusinessById(BUSINESSID_1)).thenReturn(business);

    Assertions.assertDoesNotThrow(() -> businessService.addBusinessAdmin(business.getId(), USERID_2));
  }

  /**
   * Crate a business with another user as the primary admin; should succeed as user making request is admin
   */
  @Test
  void createBusiness_tooYoung() throws UserNotFoundException, AddressValidationException, BusinessRegistrationException {
    User user2 = makeUser(EMAIL_2, PASSWORD_1, false);
    user2.setDateOfBirth(LocalDate.now().minusYears(14)); // must be over 16
    user2.setId(USERID_2);

    Business business = makeBusiness(user2);
    business.setId(BUSINESSID_1);

    when(userService.isAdmin()).thenReturn(true);
    when(userService.getUserById(USERID_2)).thenReturn(user2);
    assertThrows(BusinessRegistrationException.class, () -> businessService.saveBusiness(business));
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
    when(businessDao.getBusinessById(BUSINESSID_1)).thenReturn(business);

    when(userService.getLoggedInUser()).thenReturn(user2);
    Assertions.assertThrows(InsufficientPrivilegesException.class, () -> businessService.addBusinessAdmin(business.getId(), USERID_2));
  }

  @Test
  void removeUserBusinessAdmin_expectOk() throws Exception {
    Business business = makeBusiness();
    business.setId(BUSINESSID_1);
    business.setPrimaryAdministratorId(USERID_1);

    User user2 = makeUser(EMAIL_2, PASSWORD_1, false);
    user2.setId(USERID_2);
    when(userService.getUserById(USERID_2)).thenReturn(user2);
    businessService.addBusinessAdmin(business.getId(), USERID_2);

    Assertions.assertDoesNotThrow(() -> businessService.removeBusinessAdmin(business.getId(), USERID_2));

  }

  @Test
  void removeUserBusinessAdmin_expectInsufficientPrivileges() throws Exception {
    // User 2 trying to make himself admin
    Business business = makeBusiness();
    business.setId(BUSINESSID_1);
    business.setPrimaryAdministratorId(USERID_1);

    User user2 = makeUser(EMAIL_2, PASSWORD_1, false);
    user2.setId(USERID_2);
    when(userService.getUserById(USERID_2)).thenReturn(user2);
    businessService.addBusinessAdmin(business.getId(), USERID_2);

    when(userService.getLoggedInUser()).thenReturn(user2);
    Assertions.assertThrows(InsufficientPrivilegesException.class, () -> businessService.removeBusinessAdmin(business.getId(), USERID_2));
  }


}