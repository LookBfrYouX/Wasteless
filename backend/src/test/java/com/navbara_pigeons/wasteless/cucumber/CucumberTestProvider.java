package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.SharedHttpSessionConfigurer.sharedHttpSession;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.controller.BusinessController;
import com.navbara_pigeons.wasteless.controller.ProductController;
import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.exception.UserAuthenticationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.security.model.BasicUserDetails;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import com.navbara_pigeons.wasteless.security.service.BasicUserDetailsServiceImpl;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import io.cucumber.spring.CucumberContextConfiguration;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.PostConstruct;

@CucumberContextConfiguration
public class CucumberTestProvider extends MainTestProvider {

  @Autowired
  protected UserController userController;

  @Autowired
  protected BusinessController businessController;

  @Autowired
  protected ProductController productController;

  @Autowired
  protected BasicUserDetailsServiceImpl basicUserDetailsService;

  protected Long loggedInUserId = null;

  /**
   * Logs in as admin using userController. See `adminLogin`, may or may not be exactly the same
   */
  protected void login(String email, String password) throws Exception {
    // https://stackoverflow.com/questions/36584184/spring-security-withmockuser-does-not-work-with-cucumber-tests
    JSONObject credentials = new JSONObject();
    credentials.appendField("email", email);
    credentials.appendField("password", password);

    String stringResponse = mockMvc.perform(
        post("/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content(credentials.toJSONString())
    )
        .andExpect(status().isOk())
        .andReturn().getResponse().getContentAsString();

    loggedInUserId = objectMapper.readTree(stringResponse).get("userId").asLong();
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
   * @param endpoint
   * @param data     Java object
   * @param expect   e.g. status().getCreated(). can be null
   * @return
   * @throws Exception
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
   * @param endpoint
   * @param expect   e.g. status.getCreated(). can be null
   * @return objectMapped response
   * @throws Exception
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


}
