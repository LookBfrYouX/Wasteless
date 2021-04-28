package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.entity.User;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class U3SearchingForUsersStepdefs extends CucumberTestProvider {

    @Given("these users exist")
    public void theseUsersExist(DataTable dataTable) {
        List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
        String password = "Test12345";
        for (Map<String, String> columns : rows) {
            User newUser = makeUser(columns.get("emailAddress"), password, false);
            newUser.setFirstName(columns.get("firstName"));
            newUser.setLastName(columns.get("lastName"));
            newUser.setNickname(columns.get("nickname"));
            System.out.println("CREATED NEW USER: " + newUser.toString());
            try {
                userController.registerUser(newUser);
            } catch (Exception exc) {
                exc.printStackTrace();
            }
        }
    }

    @Given("A user {string} is logged in.")
    public void aUserIsLoggedIn(String arg0) {
    }

    @When("A search is performed for another user named {string}")
    public void aSearchIsPerformedForAnotherUserNamed(String arg0) {
    }

    @Then("A user record for user {string} is returned.")
    public void aUserRecordForUserIsReturned(String arg0) {
    }

    @When("A search is performed for another user with nickname {string}")
    public void aSearchIsPerformedForAnotherUserWithNickname(String arg0) {
    }

    @When("A search is performed for a non existent user {string}")
    public void aSearchIsPerformedForANonExistentUser(String arg0) {
    }

    @Then("No user records are returned")
    public void noUserRecordsAreReturned() {
    }

}
