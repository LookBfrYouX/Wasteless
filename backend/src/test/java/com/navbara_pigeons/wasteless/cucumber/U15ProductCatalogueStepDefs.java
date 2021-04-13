package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.controller.BusinessController;
import com.navbara_pigeons.wasteless.controller.ProductController;
import com.navbara_pigeons.wasteless.controller.UserController;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.beans.factory.annotation.Autowired;

public class U15ProductCatalogueStepDefs {

    private UserController userController;
    private BusinessController businessController;
    private ProductController productController;

    @Before
    @Autowired
    public void setup(
            UserController userController,
            BusinessController businessController,
            ProductController productController) {
        this.userController = userController;
        this.businessController = businessController;
        this.productController = productController;
    }

    @Given("a user with name {string} is logged in and administers a business called {string}")
    public void aUserWithNameIsLoggedInAndAdministersABusinessCalled(String arg0, String arg1) {
    }

    @When("{string} accesses {string} product {string}")
    public void accessesProduct(String arg0, String arg1, String arg2) {

    }

    @Then("the product {string} is displayed")
    public void theProductIsDisplayed(String arg0) {
    }

    @When("{string} accesses another business called {string} product {string}")
    public void accessesAnotherBusinessCalledProduct(String arg0, String arg1, String arg2) {
    }

    @Then("the product {string} is not displayed")
    public void theProductIsNotDisplayed(String arg0) {
    }
}
