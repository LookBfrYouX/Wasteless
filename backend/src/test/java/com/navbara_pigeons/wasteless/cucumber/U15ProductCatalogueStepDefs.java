package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.ResponseEntity;

import java.util.List;

public class U15ProductCatalogueStepDefs extends SpringIntegrationTest {

    @Before
    public void setup() {
    }

    @Given("a user with name {string} is logged in and administers a business called {string} that sells a product {string}")
    public void aUserWithNameIsLoggedInAndAdministersABusinessCalled(String userName, String businessName, String productName) {
        // TODO: figure out how to properly do cucumber tests
    }

    @When("{string} accesses {string} product {string}")
    public void accessesProduct(String userName, String businessName, String productName) {
        // TODO: as above
    }

    @Then("the product {string} is displayed")
    public void theProductIsDisplayed(String productName) {
        // TODO: as above
    }

    @When("{string} accesses another business called {string} product {string}")
    public void accessesAnotherBusinessCalledProduct(String userName, String businessName, String productName) {
        // TODO: as above
    }

    @Then("the product {string} is not displayed")
    public void theProductIsNotDisplayed(String productName) {
        System.out.println("THEN 2 >>>> " + productName);
    }

  @Given("a user with name {string} has a {string} business {string} in {string}")
  public void aUserWithNameHasABusinessIn(String userName, String businessType, String business, String country) {
    // Create user
    // Create business
  }

  @When("{string} has a product {string} to sell at business {string}")
  public void hasAProductToSellAtBusiness(String userName, String product, String business) {
      // Add product to business catalogue
  }

  @Then("the business {string} catalogue shows the product {string}")
  public void theBusinessCatalogueShowsTheProduct(String business, String product) {
      // Show product exists in business catalogue
  }
}
