package com.navbara_pigeons.wasteless.testprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

/**
 * This test helper class provides the general setup and methods available to all Service and Dao
 * tests. Test classes can extend this MainTestProvider to have access to the functionality.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class MainTestProvider {

  protected final String EMAIL_1 = "example@example.com";
  protected final String EMAIL_2 = "example2@example.com";
  protected final String BUSINESS_1_NAME = "BUSINESS";
  protected final String PASSWORD_1 = "ABCabc123!@#";
  protected final String PRODUCT_1_NAME = "PIZZA";

  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected ObjectMapper objectMapper;

  protected InventoryItem makeInventoryItem(Product product) {
    InventoryItem inventoryItem = new InventoryItem();
    inventoryItem.setproduct(product)
        .setExpires("2022-05-11")
        .setQuantity(10);
    return inventoryItem;
  }

  protected Product makeProduct(String productName) {
    Product product = new Product();
    product.setName(productName)
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
        .setDescription("A test product.")
        .setRecommendedRetailPrice(20.25);
    return product;
  }

  /**
   * This test helper method creates and returns a test business to be used in cucumber tests from
   * the given parameters. Does NOT set ID.
   *
   * @param businessName name of business
   * @return Business business
   */
  protected Business makeBusiness(String businessName) {
    Business business = new Business();
    business.setName(businessName)
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
        .setBusinessType("Non-profit organisation")
        .setAddress(makeAddress())
        .setDescription("some description");
    return business;
  }

  /**
   * This test helper method creates and returns a test business to be used in cucumber tests from
   * the given parameters, setting the primary administrator and administrators list. Does NOT set
   * Id
   *
   * @param businessName name of business
   * @param admin        primary administrator. Used to set primaryAdministratorId and is added to
   *                     administrators list
   * @return
   */
  protected Business makeBusiness(String businessName, User admin) {
    Business business = makeBusiness(businessName);
    ArrayList<User> administrators = new ArrayList<>();
    administrators.add(admin);
    business.setName(businessName)
        .setAdministrators(administrators)
        .setPrimaryAdministratorId(admin.getId());
    return business;
  }

  /**
   * This test helper method creates and returns a test business to be used in cucumber tests with
   * default business names, types, descriptions etc.
   *
   * @return Business business
   */
  protected Business makeBusiness() {
    return makeBusiness("Test Business Name");
  }

  /**
   * This test helper method creates and returns a test user to be used in cucumber tests from the
   * given parameters.
   *
   * @return user User
   */
  protected User makeUser(String email, String password, Boolean isAdmin) {
    User testUser = new User();
    // Create test user
    testUser.setId(0)
        .setFirstName("Tony")
        .setLastName("Last")
        .setMiddleName("Middle")
        .setNickname("Nick")
        .setEmail(email)
        .setPhoneNumber("+6412345678")
        .setDateOfBirth("2000-03-10")
        .setHomeAddress(makeAddress())
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
        .setRole(isAdmin ? "ROLE_ADMIN" : "ROLE_USER")
        .setPassword(encodePass(password));

    // Save user using DAO and retrieve by Email
    return testUser;
  }

  /**
   * This test helper method creates and returns a test address to be used in cucumber tests from
   * the given parameters.
   *
   * @return address Address
   */
  protected Address makeAddress() {
    Address address = new Address();
    address.setStreetNumber("3/24")
        .setStreetName("Ilam Road")
        .setPostcode("90210")
        .setCity("Christchurch")
        .setRegion("Canterbury")
        .setCountry("New Zealand");
    return address;
  }

  /**
   * This helper method encodes a password using the BCrypt encoder.
   *
   * @param password A plaintext password String to encode.
   * @return String The encoded password.
   */
  private String encodePass(String password) {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    return bCryptPasswordEncoder.encode(password);
  }

}
