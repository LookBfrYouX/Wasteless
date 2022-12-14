package com.navbara_pigeons.wasteless.cucumber;

import static org.hamcrest.Matchers.isA;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.dto.CreateUserDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.User;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class U3SearchingForUsersStepdefs extends CucumberTestProvider {

  private MvcResult mvcResult;

  @Given("these users exist")
  public void theseUsersExist(DataTable dataTable) {
    List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
    String password = "Test@1234";
    for (Map<String, String> columns : rows) {
      User newUser = makeUser(columns.get("emailAddress"), password, false);
      newUser.setFirstName(columns.get("firstName"));
      newUser.setLastName(columns.get("lastName"));
      newUser.setNickname(columns.get("nickName"));
      newUser.setPassword(columns.get("password"));
      System.out.println("CREATED NEW USER: " + newUser);
      Assertions.assertDoesNotThrow(() -> userController.registerUser(new CreateUserDto(newUser)));
    }
  }

  @Given("A user {string} with password {string} is logged in.")
  public void aUserWithPasswordIsLoggedIn(String email, String password) throws Exception {
    login(email, password);
  }

  @When("A search is performed for another user named {string}")
  public void aSearchIsPerformedForAnotherUserNamed(String name) throws Exception {
    System.out.println("CHECKING FOR A USER WITH FIRSTNAME: " + name);
    // Make a request to the endpoint searching for a name and store the result
    this.mvcResult = mockMvc.perform(get("/users/search")
        .queryParam("searchQuery", name))
        .andExpect(status().is(200))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", isA(ArrayList.class)))
        .andReturn();
  }

  @Then("A user record for user {string} is returned.")
  public void aUserRecordForUserIsReturned(String name) throws UnsupportedEncodingException {
    // Assert that the person is indeed in the returned array (given as a JSON string)
    assertTrue(this.mvcResult.getResponse().getContentAsString().contains(name));
    System.out.println(
        "USER FOUND -> " + name + " is in " + this.mvcResult.getResponse().getContentAsString());
  }

  @When("A search is performed for another user with nickname {string}")
  public void aSearchIsPerformedForAnotherUserWithNickname(String nickname) throws Exception {
    System.out.println("CHECKING FOR USER WITH NICKNAME: " + nickname);
    this.mvcResult = mockMvc.perform(get("/users/search")
        .queryParam("searchQuery", nickname))
        .andExpect(status().is(200))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", isA(ArrayList.class)))
        .andReturn();
  }

  @When("A search is performed for a non existent user {string}")
  public void aSearchIsPerformedForANonExistentUser(String name) throws Exception {
    System.out.println("CHECKING FOR NON EXISTENT USER: " + name);
    this.mvcResult = mockMvc.perform(get("/users/search")
        .queryParam("searchQuery", name))
        .andExpect(status().is(200))
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", isA(ArrayList.class)))
        .andReturn();
  }

  @Then("No user records are returned")
  public void noUserRecordsAreReturned()
      throws UnsupportedEncodingException, JsonProcessingException {
    String json = this.mvcResult.getResponse().getContentAsString();
    PaginationDto<User> paginationDto = new ObjectMapper().readValue(json, PaginationDto.class);
    assertEquals("[]", paginationDto.getResults().toString());
    System.out.println("NO USERS RETURNED -> " + this.mvcResult.getResponse().getContentAsString());
  }

}