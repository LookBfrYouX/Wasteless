package com.navbara_pigeons.wasteless.dao;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InvalidAttributeValueException;

@SuppressWarnings({"ALL", "SpellCheckingInspection"})
@SpringBootTest
class UserDaoHibernateImplTest {

  @Autowired
  UserDao userDao;

  @Autowired
  AddressDao addressDao;

  @Test
  void saveValidUserAndGetByEmail() {
    User testUser = new User();
    String testEmail = "test@uclive.ac.nz";

    // Create test user
    testUser.setFirstName("Fletcher")
        .setLastName("Dick")
        .setMiddleName("")
        .setNickname("")
        .setEmail(testEmail)
        .setDateOfBirth("2000-03-10")
        .setHomeAddress(mockAddress())
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
        .setRole("user")
        .setPassword("myPassword");

    // Save user using DAO and retrieve by Email
    userDao.saveUser(testUser);
    User returnedUser = null;
    try {
      returnedUser = userDao.getUserByEmail(testEmail);
    } catch (UserNotFoundException e) {
      e.printStackTrace();
    }

    // Assert that the returned user is the same as the test user
    assert returnedUser != null;
    assertEquals(testUser.getEmail(), returnedUser.getEmail());
    assertEquals(testUser.getFirstName(), returnedUser.getFirstName());
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  void saveValidUserAndGetById() {
    User testUser = new User();

    // Create test user
    //noinspection SpellCheckingInspection
    testUser.setFirstName("Dawson")
        .setLastName("Berry")
        .setMiddleName("Neil")
        .setNickname("dnb36")
        .setEmail("fake@uclive.ac.nz")
        .setDateOfBirth("2001-11-23")
        .setHomeAddress(mockAddress())
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
        .setRole("user")
        .setPassword("$2y$12$dhGJXGeytGMritdpvgPYCusLaldDTUUvsZXDV2g6zT4wHvtnlLsva");

    // Save user using DAO and retrieve by Id
    userDao.saveUser(testUser);
    User returnedUser = null;
    try {
      returnedUser = userDao.getUserById(testUser.getId());
    } catch (UserNotFoundException e) {
      e.printStackTrace();
    }

    // Assert that the returned user is the same as the test user
    assert returnedUser != null;
    assertEquals(testUser.getId(), returnedUser.getId());
    assertEquals(testUser.getEmail(), returnedUser.getEmail());
    assertEquals(testUser.getFirstName(), returnedUser.getFirstName());
  }

  @Test
  void searchUsersTest() {
    User testUser1 = new User();
    // Create test user
    testUser1.setFirstName("Fred")
        .setLastName("Smith")
        .setMiddleName("")
        .setNickname("Fman")
        .setEmail("fake1@uclive.ac.nz")
        .setDateOfBirth("2001-11-23")
        .setHomeAddress(mockAddress())
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
        .setRole("user")
        .setPassword("test1");

    User testUser2 = new User();
    // Create test user
    testUser2.setFirstName("Freddy")
        .setLastName("Smithson")
        .setMiddleName("John")
        .setNickname("Frodo")
        .setEmail("fake2@uclive.ac.nz")
        .setDateOfBirth("2001-11-23")
        .setHomeAddress(mockAddress())
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
        .setRole("user")
        .setPassword("test2");

    User testUser3 = new User();
    // Create test user
    testUser3.setFirstName("test")
        .setLastName("name")
        .setMiddleName("user")
        .setNickname("tt")
        .setEmail("fake3@uclive.ac.nz")
        .setDateOfBirth("2001-11-23")
        .setHomeAddress(mockAddress())
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
        .setRole("user")
        .setPassword("test3");

    userDao.saveUser(testUser1);
    userDao.saveUser(testUser2);
    userDao.saveUser(testUser3);

    List<User> results1 = null;
    try {
      results1 = userDao.searchUsers("Fred");
      assertEquals(results1.size(), 2);

      List<User> results2 = userDao.searchUsers("smiths");
      assertEquals(results2.size(), 1);
      assertEquals(results2.get(0).getId(), testUser2.getId());
      assertEquals(results2.get(0).getEmail(), testUser2.getEmail());

      List<User> results3 = userDao.searchUsers("UsEr");
      assertEquals(results3.size(), 1);
      assertEquals(results3.get(0).getId(), testUser3.getId());
      assertEquals(results3.get(0).getEmail(), testUser3.getEmail());

      List<User> results4 = userDao.searchUsers("Frod");
      assertEquals(results4.size(), 1);
      assertEquals(results4.get(0).getId(), testUser2.getId());
      assertEquals(results4.get(0).getEmail(), testUser2.getEmail());
    } catch (InvalidAttributeValueException e) {
      e.printStackTrace();
    }
  }

  @Test
  void correctExactSearchForUserTest() {
    // ARRANGE
    String firstName = "Oprah";
    String middleName = "Gail";
    String lastName = "Winfrey";
    String nickname = "Garfeild";
    // Create test user then save it to the db
    User userToSearch = new User()
        .setFirstName(firstName)
        .setMiddleName(middleName)
        .setLastName(lastName)
        .setNickname(nickname)
        .setEmail("John.Jono@uclive.ac.nz")
        .setDateOfBirth("2001-11-23")
        .setHomeAddress(mockAddress())
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
        .setRole("ROLE_USER")
        .setPassword("this_is_my_pass")
        .setBusinesses(new ArrayList<Business>());
    actuallySaveUser(userToSearch);

    // ACT
    List<User> validFnameQuery = null;
    try {
      validFnameQuery = userDao.searchUsers(firstName);
      List<User> validMnameQuery = userDao.searchUsers(middleName);
      List<User> validLnameQuery = userDao.searchUsers(lastName);
      List<User> validNnameQuery = userDao.searchUsers(nickname);
      List<User> validFullNameQuery = userDao
          .searchUsers(firstName + ' ' + middleName + ' ' + lastName);


      // ASSERT
      assertEquals(userToSearch.getId(), validFnameQuery.get(0).getId());
      assertEquals(userToSearch.getId(), validMnameQuery.get(0).getId());
      assertEquals(userToSearch.getId(), validLnameQuery.get(0).getId());
      assertEquals(userToSearch.getId(), validNnameQuery.get(0).getId());
      assertEquals(userToSearch.getId(), validFullNameQuery.get(0).getId());
    } catch (InvalidAttributeValueException e) {
      e.printStackTrace();
    }
  }

  @Transactional
  void actuallySaveUser(User user) {
    this.userDao.saveUser(user);
  }

  @Test
  void incorrectExactSearchForUserTest() {
    // ARRANGE
    String firstName = "William";
    String middleName = "Bradley";
    String lastName = "Pitt";
    String nickname = "Brado";
    // Create test user then save it to the db
    User userToSearch = new User()
        .setFirstName(firstName)
        .setMiddleName(middleName)
        .setLastName(lastName)
        .setNickname(nickname)
        .setEmail("William.Pitt@uclive.ac.nz")
        .setDateOfBirth("2001-11-23")
        .setHomeAddress(mockAddress())
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
        .setRole("ROLE_USER")
        .setPassword("this_is_my_pass");
    userDao.saveUser(userToSearch);
    // Create the expected result, empty list
    List<User> expectedResult = new ArrayList<>();

    // ACT
    List<User> invalidFnameQuery = null;
    try {
      invalidFnameQuery = userDao.searchUsers("Philliam");
      List<User> invalidMnameQuery = userDao.searchUsers("Gladley");
      List<User> invalidLnameQuery = userDao.searchUsers("PittBull");
      List<User> invalidNnameQuery = userDao.searchUsers("Lado");
      List<User> invalidFullNameQuery = userDao
          .searchUsers("Philliam Gladley PittBull");

      // ASSERT
      assertEquals(expectedResult, invalidFnameQuery);
      assertEquals(expectedResult, invalidMnameQuery);
      assertEquals(expectedResult, invalidLnameQuery);
      assertEquals(expectedResult, invalidNnameQuery);
      assertEquals(expectedResult, invalidFullNameQuery);
    } catch (InvalidAttributeValueException e) {
      e.printStackTrace();
    }
  }

  private Address mockAddress() {
    Address address = new Address()
        .setStreetNumber("3/24")
        .setStreetName("Ilam Road")
        .setPostcode("90210")
        .setCity("Christchurch")
        .setRegion("Canterbury")
        .setCountry("New Zealand");

    addressDao.saveAddress(address);
    return address;
  }
}
