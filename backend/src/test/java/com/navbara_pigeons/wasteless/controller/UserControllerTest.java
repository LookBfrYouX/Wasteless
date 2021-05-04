package com.navbara_pigeons.wasteless.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.UserDao;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.User;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
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

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {

  @Autowired
  private final AddressDao addressDao;
  @Autowired
  private final UserDao userDao;
  @Autowired
  public MockMvc mockMvc;
  @Autowired
  private WebApplicationContext wac;
  private String password = "pass";

  private User user;

  @Autowired
  UserControllerTest(AddressDao addressDao, UserDao userDao) {
    this.addressDao = addressDao;
    this.userDao = userDao;
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

  public MockHttpSession login(User user) throws Exception {
    MvcResult mvcResult = this.mockMvc.perform(
        setCorsHeaders(post("/login"))
            .header("Content-Type", "application/json")
            .content(
                "{ \"email\": \"" + user.getEmail() + "\", \"password\": \"" + password + "\"}")
    )
        .andExpect(status().isOk())
        .andReturn();

    String attributeName = "SPRING_SECURITY_CONTEXT";
    MockHttpSession session = new MockHttpSession();
    session.setAttribute(attributeName,
        mvcResult.getRequest().getSession().getAttribute(attributeName));

    return session;
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
        .setPhoneNumber("+64123456789")
        .setHomeAddress(address)
        .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
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