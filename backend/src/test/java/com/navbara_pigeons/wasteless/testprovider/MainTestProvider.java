package com.navbara_pigeons.wasteless.testprovider;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.entity.*;
import com.navbara_pigeons.wasteless.model.TransactionReportModel;
import com.navbara_pigeons.wasteless.service.BusinessService;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

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
  protected ObjectMapper objectMapper;

  @Autowired
  protected BusinessService businessService;

  // https://docs.spring.io/spring-framework/docs/current/reference/html/testing.html
  @Autowired
  protected MockMvc mockMvc;

  @Autowired
  protected WebApplicationContext webApplicationContext;

  protected TransactionReportModel makeTransactionReport(LocalDate date, Integer count, Double amount) {
    ZoneId zoneId = ZoneId.of("GMT+12:00");
    LocalTime time = LocalTime.now();
    ZonedDateTime timeStamp = ZonedDateTime.of(date, time, zoneId);
    return new TransactionReportModel(timeStamp, count, amount);
  }
  /**
   * This test helper method creates and returns listings given an inventory item.
   * @param inventoryItem
   * @return
   */
  protected Listing makeListing(InventoryItem inventoryItem) {
    Listing listing = new Listing();
    listing.setInventoryItem(inventoryItem)
        .setQuantity(6)
        .setPrice(10.00)
        .setMoreInfo("Mocked more info")
        .setCreated(ZonedDateTime.now())
        .setCloses(ZonedDateTime.now().plusDays(12))
        .setQuantity(1)
        .setPrice(12.0);
    return listing;
  }

  protected InventoryItem makeInventoryItem(Product product, Business business) {
    InventoryItem inventoryItem = new InventoryItem();
    inventoryItem.setProduct(product)
        .setExpires(LocalDate.now().plusDays(12))
        .setQuantity(10)
        .setPricePerItem(2.00)
        .setPricePerItem(10.00)
        .setBusiness(business);
    return inventoryItem;
  }

  protected Product makeProduct(String productName) {
    Product product = new Product();
    product.setName(productName)
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
        .setDescription("A test product.")
        .setManufacturer("Test manufacturer")
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
        .setBusinessType(BusinessType.NON_PROFIT)
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
    business
        .setName(businessName)
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
   * This test helper method creates and returns a test business to be used in cucumber tests with
   * default business names, types, descriptions etc.
   *
   * @param admin primary business administrator
   * @return Business business
   */
  protected Business makeBusiness(User admin) {
    return makeBusiness("Test Business Name", admin);
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
    testUser
        .setFirstName("Tony")
        .setLastName("Last")
        .setMiddleName("Middle")
        .setNickname("Nick")
        .setEmail(email)
        .setPhoneNumber("+6412345678")
        .setDateOfBirth(LocalDate.parse("2000-03-10"))
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
        .setSuburb("Ilam")
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

  /**
   * This test helper method creates and returns a test Keyword to be used in CreateKeywordDtoTest
   *
   * @return Keyword entity with a valid length name
   */
  protected Keyword makeKeyword() {
    Keyword keyword = new Keyword();
    keyword.setName("Random Keyword");
    return keyword;
  }
}
