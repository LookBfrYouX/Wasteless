package com.navbara_pigeons.wasteless.cucumber;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.navbara_pigeons.wasteless.dto.CreateUserDto;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.User;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class U1RegisteringAndLogginInStepdefs extends CucumberTestProvider {

  private MvcResult mvcResult;

  @Given("this user exist")
  public void thisUserExists(DataTable dataTable) {
    List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> columns : rows) {
      User newUser = makeUser(columns.get("emailAddress"), "temp", false);
      //password is hashed in makeUser but registerUser hashes the password of user provided already
      newUser.setPassword(columns.get("password"));
      newUser.setFirstName(columns.get("firstName"));
      newUser.setLastName(columns.get("lastName"));
      newUser.setNickname(columns.get("nickname"));
      System.out.println("CREATED NEW USER: " + newUser.toString());
      Assertions.assertDoesNotThrow(() -> userController.registerUser(new CreateUserDto(newUser)));
    }
  }

  @When("I log in with valid email {string} and valid password {string}")
  public void iLogInWithValidUsernameAndPassword(String email, String password)
      throws Exception {
    JSONObject credentials = new JSONObject();
    credentials.put("email", email);
    credentials.put("password", password);
    System.out.println(credentials.toString());
    this.mvcResult = mockMvc.perform(
        post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(credentials.toString())
            .accept(MediaType.ALL))
        .andExpect(status().is(200))
        .andReturn();
  }

  @Then("I am logged in as user {string}")
  public void iAmLoggedInAsUser(String email) throws Exception {
    //TODO figure out how to get and check cookies
    JSONObject userId = new JSONObject(this.mvcResult.getResponse().getContentAsString());
    System.out.println(userId.get("userId"));
    String id = userId.get("userId").toString();
    this.mvcResult = mockMvc.perform(get("/users/{id}", id)
        .content(id))
        .andExpect(status().isOk()).andReturn();

    JSONObject result = new JSONObject(this.mvcResult.getResponse().getContentAsString());
    assertEquals(result.get("email"), email);
  }

  @When("I log in with invalid email {string} and password {string} combination")
  public void iLogInWithInvalidUsernameAndPasswordCombination(String email, String password)
      throws Exception {
    JSONObject credentials = new JSONObject();
    credentials.put("email", email);
    credentials.put("password", password);
    this.mvcResult = mockMvc.perform(
        post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(credentials.toString())
            .accept(MediaType.ALL))
        .andExpect(status().is(400))
        .andReturn();
  }

  @Then("I am shown an error that my email or password is not valid")
  public void iAmShownAnErrorThatMyUsernameOrPasswordIsNotValid() {
    assertEquals("Failed login attempt, email or password incorrect",
        this.mvcResult.getResponse().getErrorMessage());
  }

  @When("I register an account with the valid email {string} and password {string}")
  public void iRegisterAnAccountWithValidEmailAndPassword(String email, String password)
      throws Exception {
    User newUser = makeUser(email, password, false);
    newUser.setPassword(password);
    this.mvcResult = mockMvc.perform(
        post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newUser))
            .accept(MediaType.ALL))
        .andExpect(status().is(201))
        .andReturn();
  }

  @When("I register an account with the invalid email {string} and password {string}")
  public void iRegisterAnAccountWithTheInvalidEmailAndPassword(String email, String password)
          throws Exception {
    User newUser = makeUser(email, password, false);
    newUser.setPassword(password);
    this.mvcResult = mockMvc.perform(
            post("/users")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(newUser))
                    .accept(MediaType.ALL))
            .andExpect(status().is(400))
            .andReturn();
  }



  @Then("I am shown an error that my request is invalid")
  public void iAmShownAnErrorThatMyRequestIsInvalid() {
    assertEquals(400, this.mvcResult.getResponse().getStatus());
  }

  @When("I register an account with the taken email {string} and password {string}")
  public void iRegisterAnAccountWithTheTakenEmailAndPassword(String email, String password)
      throws Exception {
    User newUser = makeUser(email, password, false);
    newUser.setPassword(password);
    this.mvcResult = mockMvc.perform(
        post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newUser))
            .accept(MediaType.ALL))
        .andExpect(status().is(409))
        .andReturn();
  }

  @Then("I am shown an error that my email is taken")
  public void iAmShownAnErrorThatMyEmailIsTaken() {
    assertEquals("Email address already in use", this.mvcResult.getResponse().getErrorMessage());
  }

  @When("I register an account with an invalid address")
  public void iRegisterAnAccountWithAnInvalidAddress() throws Exception {
    Address newAddress = makeAddress();
    newAddress.setCountry("asdasdasd");
    User newUser = makeUser("email@tmpusr.com", "password", false);
    newUser.setPassword("TempPassword1234");
    newUser.setHomeAddress(newAddress);
    this.mvcResult = mockMvc.perform(
        post("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(newUser))
            .accept(MediaType.ALL))
        .andExpect(status().is(400))
        .andReturn();
  }

  @Then("I am shown an error that my address is invalid")
  public void iAmShownAnErrorThatMyAddressIsInvalid() {
    assertEquals("Bad address given", this.mvcResult.getResponse().getErrorMessage());
  }
}
