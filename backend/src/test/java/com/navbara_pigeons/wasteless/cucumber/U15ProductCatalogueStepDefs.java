package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class U15ProductCatalogueStepDefs extends SpringIntegrationTest {

    private User user;
    private Business business;

    @Before
    public void setup() {
    }

    @Given("a user with name {string} is logged in and administers a business called {string}")
    public void aUserWithNameIsLoggedInAndAdministersABusinessCalled(String userName, String businessName) {
        // Set up and register user
        String email = userName.toLowerCase() + "@wasteless.co.nz";
        String password = "testPass123";
        Boolean isAdmin = false;
        this.user = makeUser(email, password, isAdmin);
        registerUser(this.user);
        System.out.println("REGISTERED USER: " + this.user.getId());
        // Log user in
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setEmail(this.user.getEmail());
        userCredentials.setPassword(password);
        login(userCredentials);
        // Set up and register a business
        this.business = makeBusiness();
    }

    @When("{string} accesses {string} product {string}")
    public void accessesProduct(String userName, String businessName, String productName) {
        System.out.println("WHEN 1 >>>> " + userName + " : " + businessName + " : " + productName);
    }

    @Then("the product {string} is displayed")
    public void theProductIsDisplayed(String productName) {
        System.out.println("THEN 1 >>>> " + productName);
    }

    @When("{string} accesses another business called {string} product {string}")
    public void accessesAnotherBusinessCalledProduct(String userName, String businessName, String productName) {
        System.out.println("WHEN 2 >>>> " + userName + " : " + businessName + " : " + productName);
    }

    @Then("the product {string} is not displayed")
    public void theProductIsNotDisplayed(String productName) {
        System.out.println("THEN 2 >>>> " + productName);
    }
}
