package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.test.web.servlet.MvcResult;

public class U19InventoryStepdefs extends CucumberTestProvider {
    private User user;
    private String businessId;
    private Product product;
    private MvcResult response;

    @Given("My business has an item called {string}")
    public void myBusinessHasAnItemCalled(String item) {

    }

    @When("I retrieve my inventory")
    public void iRetrieveMyInventory() {
    }

    @Then("The {string} is listed")
    public void theIsListed(String item) {
    }

    @When("Someone else retrieves my inventory")
    public void someoneElseRetrievesMyInventory() {
    }

    @Then("An error is shown")
    public void anErrorIsShown() {
    }

    @Given("I am logged in and have a product with id {string}")
    public void iAmLoggedInAndHaveAProductWithId(String arg0) {
    }

    @When("I add an inventory entry product id {string} with quantity {string} and expiry date in the future")
    public void iAddAnInventoryEntryProductIdWithQuantityAndExpiryDateInTheFuture(String arg0, String arg1) {
    }

    @Then("When I retrieve my inventory the entry is listed")
    public void whenIRetrieveMyInventoryTheEntryIsListed() {
    }
}
