package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.dto.BasicUserDto;
import com.navbara_pigeons.wasteless.dto.FullUserDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.UnhandledException;
import com.navbara_pigeons.wasteless.exception.UserAlreadyExistsException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserRegistrationException;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;

public class UserServiceImplTest extends ServiceTestProvider {

  @Mock
  UserDao userDaoMock;

  @Mock
  BusinessDao businessDaoMock;

  @Mock
  BCryptPasswordEncoder encoder;

  @Mock
  AddressService addressService;

  @Mock
  AuthenticationManagerBuilder authenticationManagerBuilder;

  @InjectMocks
  UserServiceImpl userService;


  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  public void saveUser_normal()
      throws UserNotFoundException, AddressValidationException, UserRegistrationException, UserAlreadyExistsException {
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    loginAuthenticationMock();
    when(encoder.encode(ArgumentMatchers.anyString())).thenReturn("HASH");
    when(userDaoMock.getUserByEmail(EMAIL_1)).thenReturn(user);
    userService.saveUser(user);
  }


  @Test
  public void saveUser_invalidDoB() {
    String[] testValues = {"31-Dec-2000", "dfs", "2020/10/05", "20/1/1"};
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    for (String testValue : testValues) {
      user.setDateOfBirth(testValue);
      assertThrows(UserRegistrationException.class, () -> userService.saveUser(user));
    }
  }

  @Test
  public void saveUser_invalidEmail() {
    String[] testValues = {"alec", "alec@", "alec@.", "alec@gmail", "alec@gmail.", "@", "@gmail",
        "@gmail.com"};
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    for (String testValue : testValues) {
      user.setEmail(testValue);
      assertThrows(UserRegistrationException.class, () -> userService.saveUser(user));
    }
  }

  @Test
  public void saveUser_invalidPassword() {
    String[] testValues = {"pwrd", "", "password",
        "passw rd", "pasWrd", "passwoRd", "passwo8d",
        "PASSW8RD"};
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    for (String testValue : testValues) {
      user.setPassword(testValue);
      assertThrows(UserRegistrationException.class, () -> userService.saveUser(user));
    }
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  public void getUserById_self() throws UserNotFoundException {
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    user.setId(100);
    when(userDaoMock.getUserById(user.getId())).thenReturn(user);

    Object userObject = userService.getUserById(user.getId());
    assertUserEquals(user, (FullUserDto) userObject);
  }

  @Test
  @WithMockUser()
  public void getUserById_otherWhenNotAdmin() throws UserNotFoundException {
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    user.setId(100);
    when(userDaoMock.getUserById(user.getId())).thenReturn(user);

    Object userObject = userService.getUserById(user.getId());
    assertUserEquals(user, (BasicUserDto) userObject);
  }

  @Test
  @WithMockUser(authorities = {"ADMIN"})
  public void getUserById_otherWhenAdmin() throws UserNotFoundException {
    User user = makeUser(EMAIL_1, PASSWORD_1, false);
    user.setId(100);
    when(userDaoMock.getUserById(user.getId())).thenReturn(user);

    Object userObject = userService.getUserById(user.getId());
    assertUserEquals(user, (FullUserDto) userObject);
  }

  @Test
  @WithMockUser()
  public void getUserById_notFound() throws UserNotFoundException {
    when(userDaoMock.getUserById(Mockito.anyLong())).thenThrow(UserNotFoundException.class);
    assertThrows(UserNotFoundException.class, () -> userService.getUserById(1000));
  }

  @Test
  @WithMockUser(username = EMAIL_1, password = PASSWORD_1)
  public void getUserById_selfIsPrimaryBusinessAdmin()
      throws UserNotFoundException, BusinessNotFoundException, UnhandledException {
    User user1 = makeUser(EMAIL_1, PASSWORD_1, false);
    user1.setId(100);
    when(userDaoMock.getUserById(user1.getId())).thenReturn(user1);

    Business business = makeBusiness(BUSINESS_1_NAME, user1);
    business.setId(101);
    when(businessDaoMock.getBusinessById(business.getId())).thenReturn(business);

    user1.addBusiness(business);

    Object userObject = userService.getUserById(user1.getId());

    assertUserEquals(user1, (FullUserDto) userObject);
  }

  @Test
  @WithMockUser()
  public void getUserById_otherIsBusinessAdmin()
      throws UserNotFoundException, BusinessNotFoundException, UnhandledException {
    User user1 = makeUser(EMAIL_1, PASSWORD_1, false);
    user1.setId(100);
    when(userDaoMock.getUserById(user1.getId())).thenReturn(user1);

    User user2 = makeUser(EMAIL_2, PASSWORD_1, false);
    user2.setId(101);
    when(userDaoMock.getUserById(user2.getId())).thenReturn(user2);

    Business business = makeBusiness(BUSINESS_1_NAME, user2);
    business.addAdministrator(user1);
    business.setId(101);
    when(businessDaoMock.getBusinessById(business.getId())).thenReturn(business);

    user1.addBusiness(business);
    user2.addBusiness(business);

    Object userObject = userService.getUserById(user1.getId());

    assertUserEquals(user1, (BasicUserDto) userObject);
  }


  /**
   * On successful signup login is called which requires this horrible thing
   */
  void loginAuthenticationMock() {
    when(authenticationManagerBuilder.getOrBuild()).thenReturn(new AuthenticationManager() {
      @Override
      public Authentication authenticate(Authentication authentication)
          throws AuthenticationException {
        return new Authentication() {
          @Override
          public Collection<? extends GrantedAuthority> getAuthorities() {
            return null;
          }

          @Override
          public Object getCredentials() {
            return null;
          }

          @Override
          public Object getDetails() {
            return null;
          }

          @Override
          public Object getPrincipal() {
            return null;
          }

          @Override
          public boolean isAuthenticated() {
            return false;
          }

          @Override
          public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {

          }

          @Override
          public String getName() {
            return null;
          }
        };
      }
    });
  }

}
