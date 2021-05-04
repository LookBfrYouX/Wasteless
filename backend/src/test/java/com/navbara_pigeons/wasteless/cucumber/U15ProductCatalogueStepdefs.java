package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;

public class U15ProductCatalogueStepdefs extends CucumberTestProvider {
  @Autowired
  private WebApplicationContext wac;
  @Autowired
  public MockMvc mockMvc;

  @Given("a user with name {string} is logged in and administers a business called {string} that sells a product {string}")
  public void aUserWithNameIsLoggedInAndAdministersABusinessCalled(String userName, String businessName, String productName) {
    // TODO: figure out how to properly do cucumber tests
    User user = this.makeUser(userName + "@example.com", "password123", true);
    user.setFirstName(userName);
    Business business = makeBusiness(businessName, user);
    Product product = this.makeProduct(productName);

  }

  @When("{string} requests his product catalogue")
  public void requestsHisProductCatalogue(String arg0) {
    
  }

  @Then("The product {string} is displayed")
  public void theProductIsDisplayed(String arg0) {
  }

  @When("{string} tries to access another business called {string} product {string}")
  public void triesToAccessAnotherBusinessCalledProduct(String arg0, String arg1, String arg2) {
  }

  @Then("the product {string} is not displayed")
  public void theProductIsNotDisplayed(String arg0) {
  }

  @Given("a user with name {string} has a {string} business {string} in {string}")
  public void aUserWithNameHasABusinessIn(String arg0, String arg1, String arg2, String arg3) {
  }

  @When("{string} creates a product {string} to sell at business {string}")
  public void createsAProductToSellAtBusiness(String arg0, String arg1, String arg2) {
  }

  @When("{string} creates a product {string} made by {string} with RRP {string}")
  public void createsAProductMadeByWithRRP(String arg0, String arg1, String arg2, String arg3) {
  }

  @Then("the ID, date created is set automatically and the currency is set to {string}")
  public void theIDDateCreatedIsSetAutomaticallyAndTheCurrencyIsSetTo(String arg0) {
  }
}
