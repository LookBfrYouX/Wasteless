package com.navbara_pigeons.wasteless.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.controller.BusinessController;
import com.navbara_pigeons.wasteless.controller.ProductController;
import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.security.service.BasicUserDetailsServiceImpl;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;

import static org.springframework.security.core.authority.AuthorityUtils.createAuthorityList;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

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


  /**
   * Logs in as admin using userController. See `adminLogin`, may or may not be exactly the same
   */
  protected void login(String email) {
    // https://stackoverflow.com/questions/36584184/spring-security-withmockuser-does-not-work-with-cucumber-tests
    SecurityContextHolder.getContext().setAuthentication(
        new UsernamePasswordAuthenticationToken(
            basicUserDetailsService.loadUserByUsername(email),
            basicUserDetailsService.loadUserByUsername(email), // This one is probably wrong but no one cares
            createAuthorityList("ADMIN")
        )
    );
  }


  /**
   * Logs in as admin using userController. See `adminLogin`, may or may not be exactly the same
   */
  protected void login() {
    login("dnb36@uclive.ac.nz");
  }

  /**
   * Make post request to endpoint with JSON data, expecting some status code and expected some JSON to get returned
   * @param endpoint
   * @param data Java object
   * @param expect e.g. status.getCreated(). can be null
   * @return
   * @throws Exception
   */
  protected JsonNode makePostRequestGetJson(String endpoint, Object data, ResultMatcher expect) throws Exception {
    ResultActions result = mockMvc.perform(
        post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(data))
    );

    if (expect != null) result = result.andExpect(expect);
    String response = result.andReturn().getResponse().getContentAsString();

    return objectMapper.readTree(response);
  }

  /**
   * Logs in as admin using userController. See `login`, may or may not be exactly the same
   */
  void adminLogin() {
    UserCredentials credentials = new UserCredentials();
    credentials.setEmail("mbi47@uclive.ac.nz");
    credentials.setPassword("fun123");
    this.userController.login(credentials);
  }
}
