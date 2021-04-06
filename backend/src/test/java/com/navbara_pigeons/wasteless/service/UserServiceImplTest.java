package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import lombok.val;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Map;

@SuppressWarnings("ALL")
@SpringBootTest
class UserServiceImplTest {

  @Autowired
  UserController userController;

  @Autowired
  UserDao userDao;

  @Autowired
  AddressDao addressDao;

  @Autowired
  UserService userService;

  @Test
  void saveValidUser() {
    // Create test user, ensure no errors are thrown
    User testUser = makeUser();
    testUser.setEmail("test@gmail.com");
    Assertions.assertDoesNotThrow(() -> userService.saveUser(testUser));
  }

  @SuppressWarnings("SpellCheckingInspection")
  @Test
  void saveInvalidUser() {
    // Test invalid dob fields
    String[] dobTests = {"31-Dec-2000", "dfs", "2020/10/05", "20/1/1", "2020-10-31"};
    User testUserDob = makeUser();
    for (String dobTest : dobTests) {
      testUserDob.setDateOfBirth(dobTest);
      assertThrows(Exception.class, () -> userService.saveUser(testUserDob));
    }

    // Test invalid email address
    String[] emailTests = {"alec", "alec@", "alec@.", "alec@gmail", "alec@gmail.", "@", "@gmail",
            "@gmail.com", "fdi19@uclive.ac.nz"};
    User testUserEmail = makeUser();
    for (String emailTest : emailTests) {
      testUserEmail.setEmail(emailTest);
      assertThrows(Exception.class, () -> userService.saveUser(testUserEmail));
    }

    // Test invalid passwords
    //noinspection SpellCheckingInspection
    @SuppressWarnings("SpellCheckingInspection") String[] passwordTests = {"pwrd", "", "password",
            "passw rd", "pasWrd", "passwoRd", "passwo8d",
            "PASSW8RD"};
    User testUserPassword = makeUser();
    for (String passwordTest : passwordTests) {
      testUserPassword.setPassword(passwordTest);
      assertThrows(Exception.class, () -> userService.saveUser(testUserPassword));
    }
  }


  @Test
  public void getUserSelf() throws UserNotFoundException {
    String password = "password";
    User user = makeUser("test@example.com", false, password);
    userController.register(user);
    assertUserWithJson(user, getUserAsUser(user, password, user.getId()), false);
  }

  /**
   * Gets a user using credentials
   * @param authorizer user who is authorizing the action
   * @param password password of authorizer
   * @param id id of user to get
   * @return user with id `id` as JSONObject
   * @throws UserNotFoundException
   */
  private JSONObject getUserAsUser(User authorizer, String password, long id) throws UserNotFoundException {
    UserCredentials userCredentials = new UserCredentials();
    userCredentials.setEmail(authorizer.getEmail());
    userCredentials.setPassword(password);
    userController.login(userCredentials);

    return userService.getUserById(id);
  }

  void assertUserWithJson(User user, JSONObject response, boolean publicOnly) {
    assertEquals(user.getFirstName(), response.getAsString("firstName"));
    assertEquals(user.getLastName(), response.getAsString("lastName"));
    assertEquals(user.getNickname(), response.getAsString("nickname"));


    // TODO created dates
//    Assertions.assertEquals(expect.getCreated(), response.getAsString("created"));
    assertEquals(user.getMiddleName(),  response.getAsString("middleName"));
    assertEquals(user.getRole(), response.getAsString("role"));

    if (!publicOnly) {
      assertEquals(user.getEmail(), response.getAsString("email"));
      assertEquals(user.getDateOfBirth(), response.getAsString("dateOfBirth"));
      assertEquals(user.getPhoneNumber(), response.getAsString("phoneNumber"));
    } else {
      System.out.println("!");
      System.out.println(response.getAsString("email"));
      Assertions.assertNull(response.get("email"));
      Assertions.assertNull(response.get("dateOfBirth"));
      Assertions.assertNull(response.get("phoneNumber"));
    }

    Address expectAddress = user.getHomeAddress();
    JSONObject responseAddress = (JSONObject) response.get("homeAddress");

    if (!publicOnly) {
      assertEquals(expectAddress.getStreetNumber(), response.getAsString("streetNumber"));
      assertEquals(expectAddress.getStreetName(), response.getAsString("streetName"));
      assertEquals(expectAddress.getPostcode(), response.getAsString("postcode"));
    } else {
      Assertions.assertNull(response.get("streetNumber"));
      Assertions.assertNull(response.get("streetName"));
      Assertions.assertNull(response.get("postcode"));
    }

    assertEquals(expectAddress.getCity(), response.getAsString("city"));
    assertEquals(expectAddress.getRegion(), response.getAsString("region"));
    assertEquals(expectAddress.getCountry(), response.getAsString("country"));
  }

  @Test
  void login() {
    // Check that no credentials or incorrect credentials throw an error.
    assertThrows(Exception.class, () -> userController.login(new UserCredentials()));

    // Set up a valid user and credentials
    // -- Set up credentials
    UserCredentials testCredentials = new UserCredentials();
    testCredentials.setEmail("test@test.com");
    testCredentials.setPassword("pass");
    // Make a user and save with encoded password
    User testUser = makeUser();
    testUser.setEmail("test@test.com");
    testUser.setPassword(encodePass("pass"));
    userDao.saveUser(testUser);
    // Check that login gives a response.
    assertNotNull(userController.login(testCredentials));
    // Check that no error is thrown.
    assertDoesNotThrow(() -> {
      userController.login(testCredentials);
    });
    // Check that the response is the correct JSONObject
    JSONObject properResponse = new JSONObject();
    try {
      properResponse.put("userId", userDao.getUserByEmail(testCredentials.getEmail()).getId());
      assertEquals(properResponse, userController.login(testCredentials).getBody());
    } catch (UserNotFoundException e) {
    }

  }

  @Test
  void makeAdminTest() {
    // Make two users (admin/non-admin)
    User adminUser = makeUser();
    User toBeAdminUser = makeUser();
    adminUser.setRole("ROLE_ADMIN");
    adminUser.setEmail("admin@test.com");
    toBeAdminUser.setEmail("toBeAdmin@test.com");

    // Persist users
    userDao.saveUser(adminUser);
    userDao.saveUser(toBeAdminUser);

    // Get users from db
    UserCredentials userCredentials = new UserCredentials();
    try {
      adminUser = userService.getUserByEmail("admin@test.com");
      toBeAdminUser = userService.getUserByEmail("toBeAdmin@test.com");

      // Log admin user in
      userCredentials.setEmail("admin@test.com");
      userCredentials.setPassword("pass");
      userController.login(userCredentials);

      // Give admin permission to user
      long toBeAdminId = toBeAdminUser.getId();
      assertDoesNotThrow(() -> userService.makeUserAdmin(toBeAdminId));

      // Refresh the user and test for admin permission
      toBeAdminUser = userService.getUserByEmail("toBeAdmin@test.com");
      assertEquals("ROLE_ADMIN", toBeAdminUser.getRole());

      // Log in as revokee and test exceptions
      userCredentials.setEmail("toBeAdmin@test.com");
      userCredentials.setPassword("pass");
      userController.login(userCredentials);

      // Test for green flow
      assertDoesNotThrow(() -> userController.revokeAdminPermissions(Long.toString(toBeAdminId)));

    } catch (UserNotFoundException e) {
      System.out.println("EXPECTED ERROR");
    }
  }

  @Test
  void revokeAdminTest() {
    // Make two users (revoker/revokee)
    User revokerUser = makeUser();
    User revokeeUser = makeUser();
    // Give admin roles
    revokerUser.setRole("ROLE_ADMIN");
    revokerUser.setEmail("revoker@test");
    revokerUser.setPassword(encodePass("pass"));
    revokeeUser.setRole("ROLE_ADMIN");
    revokeeUser.setEmail("revokee@test");
    revokeeUser.setPassword(encodePass("pass"));
    // Persist users
    userDao.saveUser(revokerUser);
    userDao.saveUser(revokeeUser);
    // Get users from db
    UserCredentials userCredentials = new UserCredentials();
    try {
      revokerUser = userService.getUserByEmail("revoker@test");
      revokeeUser = userService.getUserByEmail("revokee@test");
      System.out.println("REVOKER USER: " + revokerUser.getId());
      System.out.println("REVOKEE USER: " + revokeeUser.getId());

      // Log admin user in
      userCredentials.setEmail("revoker@test");
      userCredentials.setPassword("pass");
      userController.login(userCredentials);

      // Revoke revokee admin permission
      long revokeeId = revokeeUser.getId();
      assertDoesNotThrow(() -> userService.revokeAdmin(revokeeId));

      // Refresh the user and test for admin permission
      revokeeUser = userService.getUserByEmail("revokee@test");
      assertFalse(revokeeUser.getRole().contains("ROLE_ADMIN"));

      // Log in as revokee and test exceptions
      userCredentials.setEmail("revokee@test");
      userCredentials.setPassword("pass");
      userController.login(userCredentials);

      // Test for permission denied (as revokee is no longer admin)
      long revokerId = revokerUser.getId();
      assertThrows(Exception.class,
              () -> userController.revokeAdminPermissions(Long.toString(revokerId)));

    } catch (UserNotFoundException e) {
      System.out.println("EXPECTED ERROR");
    }
  }

  String encodePass(String password) {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    return bCryptPasswordEncoder.encode(password);
  }

  /**
   * Makes a non-admin user
   * @return
   */
  User makeUser() {
    return makeUser("test@example.com", false, "password");
  }

  /**
   * Creates test user with given details
   * @param email
   * @param isAdmin
   * @param password
   * @return
   */
  User makeUser(String email, boolean isAdmin, String password) {
    User testUser = new User();
    Address address = new Address()
            .setStreetNumber("3/24")
            .setStreetName("Ilam Road")
            .setPostcode("90210")
            .setCity("Christchurch")
            .setRegion("Canterbury")
            .setCountry("New Zealand");

    addressDao.saveAddress(address);

    // Create test user
    testUser.setId(0)
            .setFirstName("Tony")
            .setLastName("Last")
            .setMiddleName("Middle")
            .setNickname("Nick")
            .setEmail(email)
            .setPhoneNumber("+6412345678")
            .setDateOfBirth("2000-03-10")
            .setHomeAddress(address)
            .setCreated("2020-07-14T14:32:00Z")
            .setRole(isAdmin? "ROLE_ADMIN": "ROLE_USER")
            .setPassword(encodePass(password));

    // Save user using DAO and retrieve by Email
    return testUser;
  }

}