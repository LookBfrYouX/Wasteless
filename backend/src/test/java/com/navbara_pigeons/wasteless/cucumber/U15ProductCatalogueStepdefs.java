package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

public class U15ProductCatalogueStepdefs {
  @Autowired
  private WebApplicationContext wac;
  @Autowired
  public MockMvc mockMvc;


  @Before
  public void setup() {
    DefaultMockMvcBuilder builder = MockMvcBuilders
            .webAppContextSetup(this.wac)
            .apply(SecurityMockMvcConfigurers.springSecurity())
            .dispatchOptions(true);
    this.mockMvc = builder.build();
  }

  @Given("a user with name {string} is logged in and administers a business called {string} that sells a product {string}")
  public void aUserWithNameIsLoggedInAndAdministersABusinessCalled(String userName, String businessName, String productName) {
    // TODO: figure out how to properly do cucumber tests
    User user = this.makeUser(userName + "@example.com", "password123", true);
    user.setFirstName(userName);
    Business business = this.makeBusiness(businessName, user);
    Product product = this.makeProduct(productName);

  }
  @Given("{string} is signed in and administers a business {string} with a product {string}")
  public void is_signed_in_and_administers_a_business_with_a_product(String string, String string2, String string3) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();

  }

  @When("{string} requests his product catalogue")
  public void requests_his_product_catalogue(String string) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }
  @Then("The product {string} is displayed")
  public void the_product_is_displayed(String string) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }
  @When("{string} tries to access another business called {string} product {string}")
  public void tries_to_access_another_business_called_product(String string, String string2, String string3) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }
  @Then("the product {string} is not displayed")
  public void the_product_is_not_displayed(String string) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }
}
