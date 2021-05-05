package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.entity.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.test.web.servlet.MvcResult;

public class U15ProductCatalogueStepdefs extends CucumberTestProvider {

  private MvcResult mvcResult;
  private User user;

  @Given("a user with name {string} exists and already administers a business called {string} that sells a product {string}")
  public void aUserWithNameIsLoggedInAndAdministersABusinessCalled(String userName,
      String businessName, String productName) throws Exception {

  }

  @When("the user with email address {string} and password {string} logs in and requests his product catalogue with business id {string}")
  public void requestsHisProductCatalogue(String userEmail, String password, String Businessid)
      throws Exception {

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

  @When("{string} requests his product catalogue")
  public void requestsHisProductCatalogue(String arg0) {
  }
}
