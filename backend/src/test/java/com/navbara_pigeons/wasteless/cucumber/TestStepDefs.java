package com.navbara_pigeons.wasteless.cucumber;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class TestStepDefs {

    @When("I run this test.")
    public void i_run_this_test() {
        // Write code here that turns the phrase above into concrete actions
        System.out.println("This is has run!");
    }


    @Then("Something happens")
    public void something_happens() {
        // Write code here that turns the phrase above into concrete actions
        System.out.println("And something happened!");
    }

}
