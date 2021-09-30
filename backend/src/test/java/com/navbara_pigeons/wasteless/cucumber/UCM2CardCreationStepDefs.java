package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.navbara_pigeons.wasteless.dto.CreateMarketListingDto;
import com.navbara_pigeons.wasteless.dto.CreateUserDto;
import com.navbara_pigeons.wasteless.entity.User;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class UCM2CardCreationStepDefs extends CucumberTestProvider {

  @Given("There is a user")
  public void thereIsAUser(DataTable dataTable) {
    List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> columns : rows) {
      User newUser = makeUser(columns.get("emailAddress"), columns.get("password"), false);
      newUser.setFirstName(columns.get("firstName"));
      newUser.setLastName(columns.get("lastName"));
      newUser.setNickname(columns.get("nickName"));
      newUser.setPassword(columns.get("password"));
      Assertions.assertDoesNotThrow(() -> registerUser(new CreateUserDto(newUser)));
    }
  }

  // ----- AC1 -----

  @Given("A user is logged in with email {string} and password {string}")
  public void aUserIsLoggedInWithEmailAndPassword(String email, String password) throws Exception {
    login(email, password);
  }

  @When("I create a card {string} for a section {string}")
  public void iCreateACardForASection(String cardTitle, String section) throws Exception {
    CreateMarketListingDto marketListing = new CreateMarketListingDto();
    List<Long> keywordIds = new ArrayList<>(List.of(1L));
    marketListing.setCreatorId(loggedInUserId)
        .setSection(section)
        .setTitle(cardTitle)
        .setKeywordIds(keywordIds);
    mockMvc.perform(post("/cards/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(marketListing)));
  }

  @Then("The card {string} is created in {string} section and stored")
  public void theCardIsCreatedAndStored(String cardTitle, String section) throws Exception {
    MvcResult result = mockMvc.perform(get("/cards").queryParam("section", section))
        .andExpect(status().isOk()).andReturn();
    Assertions.assertTrue(result.getResponse().getContentAsString().contains(cardTitle));
  }
}