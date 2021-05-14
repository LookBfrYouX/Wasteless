package com.navbara_pigeons.wasteless.cucumber;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class U19InventoryStepdefs {
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
}
