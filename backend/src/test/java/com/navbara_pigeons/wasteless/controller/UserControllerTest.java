package com.navbara_pigeons.wasteless.controller;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.User;
import net.minidev.json.parser.JSONParser;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.BasicJsonParser;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.Map;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  @Autowired
  private WebApplicationContext wac;
  @Autowired
  public MockMvc mockMvc;
  @Autowired
  private final AddressDao addressDao;
  @Autowired
  private final UserDao userDao;

  User user;
  private String password = "pass";

  @Autowired
  UserControllerTest(AddressDao addressDao, UserDao userDao) {
    this.addressDao = addressDao;
    this.userDao = userDao;
    user = makeUser();
    userDao.saveUser(user);
  }


  @Before
  public void setup() {
    DefaultMockMvcBuilder builder = MockMvcBuilders
        .webAppContextSetup(this.wac)
        .apply(SecurityMockMvcConfigurers.springSecurity())
        .dispatchOptions(true);
    this.mockMvc = builder.build();
  }

  public MockHttpServletRequestBuilder setCorsHeaders(MockHttpServletRequestBuilder request) {
    return request
      .header("Access-Control-Request-Method", "POST")
      .header("Origin", "http://localhost:9500");
  }

  public MockHttpSession login() throws Exception {
    MvcResult mvcResult = this.mockMvc.perform(
        setCorsHeaders(post("/login"))
        .header("Content-Type", "application/json")
        .content("{ \"email\": \"" + user.getEmail() + "\", \"password\": \"" + password + "\"}")
    )
        .andExpect(status().isOk())
        .andReturn();

    String attributeName = "SPRING_SECURITY_CONTEXT";
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(attributeName, mvcResult.getRequest().getSession().getAttribute(attributeName));

    return session;
  }

  @Test
  public void testGetOwnDetails() throws Exception {
    MockHttpSession session = login();
    MvcResult mvcResult = this.mockMvc.perform(
            setCorsHeaders(get("/users/" + user.getId()))
            .session(session)
    )
            .andExpect(status().isOk())
            .andReturn();

    String response = mvcResult.getResponse().getContentAsString();
    BasicJsonParser parser = new BasicJsonParser();
    Map<String, Object> parsed = parser.parseMap(response);
    userDetailsPresent(user, parsed, false);
  }

  public void userDetailsPresent(User expect, Map<String, Object> receive, boolean publicOnly) throws AssertionError {
    Assertions.assertEquals(expect.getFirstName(), (String) receive.get("firstName"));
    Assertions.assertEquals(expect.getLastName(), (String) receive.get("lastName"));
    Assertions.assertEquals(expect.getNickname(), (String) receive.get("nickname"));


    // TODO created dates
//    Assertions.assertEquals(expect.getCreated(), (String) receive.get("created"));
    Assertions.assertEquals(expect.getMiddleName(), (String)  receive.get("middleName"));
    Assertions.assertEquals(expect.getRole(), (String) receive.get("role"));

    if (!publicOnly) {
      Assertions.assertEquals(expect.getEmail(), (String) receive.get("email"));
      Assertions.assertEquals((String) expect.getDateOfBirth(), receive.get("dateOfBirth"));
    }

    Address expectAddress = expect.getHomeAddress();
    Map<String, Object> receivedAddress = (Map<String, Object>) receive.get("homeAddress");

    if (!publicOnly) {
      Assertions.assertEquals(expectAddress.getStreetNumber(), (String) receivedAddress.get("streetNumber"));
      Assertions.assertEquals(expectAddress.getStreetName(), (String) receivedAddress.get("streetName"));
      Assertions.assertEquals(expectAddress.getPostcode(), (String) receivedAddress.get("postcode"));
    }

    Assertions.assertEquals(expectAddress.getCity(), (String) receivedAddress.get("city"));
    Assertions.assertEquals(expectAddress.getRegion(), (String) receivedAddress.get("region"));
    Assertions.assertEquals(expectAddress.getCountry(), (String) receivedAddress.get("country"));
  }

  private final User makeUser() {
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
            .setEmail("test@example.com")
            .setDateOfBirth("2000-03-10")
            .setHomeAddress(address)
            .setCreated("2020-07-14T14:32:00Z")
            .setRole("ROLE_USER")
            .setPassword(encodePass(password));

    // Save user using DAO and retrieve by Email
    return testUser;
  }

  String encodePass(String password) {
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    return bCryptPasswordEncoder.encode(password);
  }

}