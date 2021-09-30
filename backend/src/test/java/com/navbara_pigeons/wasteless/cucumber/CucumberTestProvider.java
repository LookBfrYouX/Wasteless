package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;
import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.controller.BusinessController;
import com.navbara_pigeons.wasteless.controller.ProductController;
import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.dto.CreateUserDto;
import com.navbara_pigeons.wasteless.service.InventoryService;
import com.navbara_pigeons.wasteless.service.ListingService;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import io.cucumber.spring.CucumberContextConfiguration;
import javax.annotation.PostConstruct;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@CucumberContextConfiguration
public class CucumberTestProvider extends MainTestProvider {

  @Autowired
  protected UserController userController;

  @Autowired
  protected BusinessController businessController;

  @Autowired
  protected ProductController productController;

  @Autowired
  protected InventoryService inventoryService;

  @Autowired
  protected ListingService listingService;

  protected Long loggedInUserId = null;

  @PostConstruct
  public void initMockMvc() {
    // https://stackoverflow.com/questions/38755727/in-spring-mockmvc-tests-how-to-chain-visit-of-several-webpages
    // Without the sharedHttpSession, login cookies aren't carried over between calls
    this.mockMvc = MockMvcBuilders
        .webAppContextSetup(this.webApplicationContext)
        .apply(sharedHttpSession())
        .build();
  }

  /**
   * Logs in as admin using userController. See `adminLogin`, may or may not be exactly the same
   */
  protected void login(String email, String password) throws Exception {
    // https://stackoverflow.com/questions/36584184/spring-security-withmockuser-does-not-work-with-cucumber-tests
    JSONObject credentials = new JSONObject();
    credentials.appendField("email", email);
    credentials.appendField("password", password);

    JsonNode response = makePostRequestGetJson("/login", credentials, status().isOk());

    loggedInUserId = response.get("userId").asLong();
  }

  /**
   * Logs in as admin user
   */
  protected void adminLogin() throws Exception {
    login("mbi47@uclive.ac.nz", "fun123");
  }

  /**
   * Logs in as non admin user
   */
  protected void nonAdminLogin() throws Exception {
    login("fdi19@uclive.ac.nz", "fun123");
  }

  /**
   * Make post request to endpoint with JSON data, expecting some status code and expected some JSON
   * to get returned
   *
   * @param endpoint endpoint to query
   * @param data     Java object
   * @param expect   e.g. status().getCreated(). can be null
   * @return tree of response
   * @throws Exception any exception (if exception is thrown, test should fail)
   */
  protected JsonNode makePostRequestGetJson(String endpoint, Object data, ResultMatcher expect)
      throws Exception {
    ResultActions result = mockMvc.perform(
        post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(data))
    );

    if (expect != null) {
      result = result.andExpect(expect);
    }
    String response = result.andReturn().getResponse().getContentAsString();

    return objectMapper.readTree(response);
  }

  /**
   * Make get request to endpoint, expecting some status code and expected some JSON to get
   * returned
   *
   * @param endpoint endpoint to query
   * @param expect   e.g. status.getCreated(). can be null
   * @return objectMapped response
   * @throws Exception any exception (if exception is thrown, test should fail)
   */
  protected JsonNode makeGetRequestGetJson(String endpoint, ResultMatcher expect) throws Exception {
    ResultActions result = mockMvc.perform(
        get(endpoint).contentType(MediaType.APPLICATION_JSON)
    );

    if (expect != null) {
      result = result.andExpect(expect);
    }
    String response = result.andReturn().getResponse().getContentAsString();

    return objectMapper.readTree(response);
  }

  /**
   * Registers and logs in as the given user
   *
   * @param user new user to create
   * @return user id
   * @throws Exception any exception (if exception is thrown, test should fail)
   */
  protected long registerUser(CreateUserDto user) throws Exception {
    return makePostRequestGetJson(
        "/users",
        user,
        status().isCreated()
    ).get("userId").asLong();
  }

}
