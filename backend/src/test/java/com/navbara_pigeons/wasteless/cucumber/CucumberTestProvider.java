package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.fasterxml.jackson.databind.JsonNode;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

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
  protected void login(String email) {
    // https://stackoverflow.com/questions/36584184/spring-security-withmockuser-does-not-work-with-cucumber-tests
    UserDetails user = basicUserDetailsService.loadUserByUsername(email);
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(
            user,
            user,
            // This one is probably wrong but no one cares
            createAuthorityList("ADMIN")
        )
    );
    loggedInUserId = ((BasicUserDetails) user).getId();
  }


  /**
   * Logs in as admin using userController. See `adminLogin`, may or may not be exactly the same
   */
  protected void login() {
    login("dnb36@uclive.ac.nz");
  }

  /**
   * Make post request to endpoint with JSON data, expecting some status code and expected some JSON
   * to get returned
   *
   * @param endpoint
   * @param data     Java object
   * @param expect   e.g. status.getCreated(). can be null
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

  /**
   * Logs in as admin using userController. See `login`, may or may not be exactly the same
   */
  void adminLogin() throws UserNotFoundException, UserAuthenticationException {
    UserCredentials credentials = new UserCredentials();
    credentials.setEmail("mbi47@uclive.ac.nz");
    credentials.setPassword("fun123");
    ResponseEntity<JSONObject> response = this.userController.login(credentials);
    loggedInUserId = (Long) response.getBody().get("userId");
  }

  /**
   * Logs in as non admin user using userController. See `login`, may or may not be exactly the
   * same
   */
  void nonAdminLogin(String email) throws UserNotFoundException, UserAuthenticationException {
    UserCredentials credentials = new UserCredentials();
    credentials.setEmail(email);
    credentials.setPassword("fun123");
    ResponseEntity<JSONObject> response = this.userController.login(credentials);
    loggedInUserId = (Long) response.getBody().get("userId");
  }

  void nonAdminLogin() throws UserNotFoundException, UserAuthenticationException {
    nonAdminLogin("fdi19@uclive.ac.nz");
  }


}
