package com.navbara_pigeons.wasteless.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class EU3SearchListingsByNutritionInfoStepDefs {
    @When("I send a valid request to {string} with fat set to {string}")
    void iSendAValidRequestToWithFatSetTo(String endPointUrl, String fatValue) {
    }

    @Then("only the products with fat set to {string} are shown")
    void onlyTheProductsWithFatSetToAreShown(String fatValue) {
    }

    @When("I send a valid request to {string} with saturatedFat set to {string} and {string}")
    void iSendAValidRequestToWithSaturatedFatSetToAnd(String endPointUrl, String saturatedFatValue1, String saturatedFatValue2) {
    }

    @Then("only the products with saturatedFat set to {string} and {string} are shown")
    void onlyTheProductsWithSaturatedFatSetToAndAreShown(String saturatedFatValue1, String saturatedFatValue2) {
    }

    @When("I send a valid request to {string} with salt set to {string}")
    void iSendAValidRequestToWithSaltSetTo(String endPointUrl, String saltValue) {
    }

    @Then("only the products with salt set to {string} are shown")
    void onlyTheProductsWithSaltSetToAreShown(String saltValue) {
    }

    @When("I send a valid request to {string} with  sugars set to {string}")
    void iSendAValidRequestToWithSugarsSetTo(String endPointUrl, String sugarsValue) {
    }

    @Then("only the products with sugars set to {string} are shown")
    void onlyTheProductsWithSugarsSetToAreShown(String sugarsValue) {
    }
}
