package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.ResponseEntity;

public class U1RegisteringAndLogginInStepdefs extends CucumberTestProvider {

  @Given("this user exist")
  public void thisUserExists(DataTable dataTable) {
    //called before each scenario so need to not always create user?
    List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> columns : rows) {
      User newUser = makeUser(columns.get("emailAddress"), "temp", false);
      //password is hashed in makeUser but registerUser hashes the password of user provided already
      newUser.setPassword(columns.get("password"));
      newUser.setFirstName(columns.get("firstName"));
      newUser.setLastName(columns.get("lastName"));
      //the last value in the table always seems to be null
      newUser.setNickname(columns.get("nickname"));
      System.out.println("CREATED NEW USER: " + newUser.toString());
      Assertions.assertDoesNotThrow(() -> userController.registerUser(newUser));
    }
  }

  @When("I log in with username {string} and password {string}")
  public void iLogInWithValidUsernameAndPassword(String username, String password) {
    UserCredentials login = new UserCredentials();
    login.setEmail(username);
    login.setPassword(password);
    System.out.println("LOGGED IN TO USER : " + login.getEmail());
    Assertions.assertDoesNotThrow(() -> userController.login(login));
  }

  @Then("I am logged in")
  public void iAmLoggedIn() {
    //would need to somehow check the token returned previously
  }

  @And("I am taken to my profile page")
  public void iAmTakenToMyProfilePage() {
    //does cucumber even handle frontend?
  }

  @Then("I am shown an error that my username or password is not valid")
  public void iAmShownAnErrorThatMyUsernameOrPasswordIsNotValid() {
  }

  @Then("I am prompted to enter and username")
  public void iAmPromptedToEnterAndUsername() {
  }

  @Then("I am prompted to enter a password")
  public void iAmPromptedToEnterAPassword() {
  }

  @Then("A message displays instructing me to try again later")
  public void aMessageDisplaysInstructingMeToTryAgainLater() {
  }

  @When("I submit my credentials")
  public void iSubmitMyCredentials() {
  }
}
