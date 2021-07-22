package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.dto.CreateUserDto;
import com.navbara_pigeons.wasteless.dto.FullAddressDto;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.entity.User;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class U5CreatingBusinessAccountsStepDefs extends CucumberTestProvider {

  private MvcResult response;
  private JsonNode jsonResponse;
  private Long businessId;

  @Given("this user exists")
  public void thisUserExists(DataTable dataTable) {
    List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> columns : rows) {
      User newUser = makeUser(columns.get("emailAddress"), columns.get("password"), false);
      newUser.setFirstName(columns.get("firstName"));
      newUser.setLastName(columns.get("lastName"));
      newUser.setNickname(columns.get("nickName"));
      newUser.setPassword(columns.get("password"));
      Assertions.assertDoesNotThrow(() -> userController.registerUser(new CreateUserDto(newUser)));
    }
  }

  @When("I am logged in with email {string} and password {string}")
  public void iAmLoggedInWithEmailAndPassword(String email, String password) throws Exception {
    login(email, password);
  }

  @Given("I create a {string} business {string}")
  public void iCreateABusiness(String businessType, String businessName)
      throws Exception {
    CreateBusinessDto business = new CreateBusinessDto();
    FullAddressDto address = new FullAddressDto(makeAddress());
    business.setBusinessType(businessType)
        .setName(businessName)
        .setAddress(address)
        .setPrimaryAdministratorId(loggedInUserId);

    jsonResponse = makePostRequestGetJson("/businesses", business, status().isCreated());
  }

  @Then("The business {string} is created and stored")
  public void theBusinessIsCreatedAndStored(String businessName) throws Exception {
    // Get the businessId from the returned string object
    businessId = jsonResponse.get("businessId").asLong();

    JsonNode responseObj = makeGetRequestGetJson("/businesses/" + businessId, status().isOk());
    Assertions.assertEquals(businessName, responseObj.get("name").asText());
  }

  @Given("I create a business {string} without the required field: BusinessType")
  public void iCreateABusinessWithoutTheRequiredFieldBusinessType(String businessName)
      throws Exception {
    CreateBusinessDto business = new CreateBusinessDto();
    FullAddressDto address = new FullAddressDto(makeAddress());
    business.setName(businessName)
        .setAddress(address);

    response = mockMvc.perform(post("/businesses/")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(business)))
        .andReturn();
  }

  @Then("The business is not created and an error is thrown")
  public void theBusinessIsNotCreatedAndAnErrorIsThrown() {
    if (!(response.getResponse().getStatus() == HttpServletResponse.SC_BAD_REQUEST)) {
      Assert.fail("Request should have failed for registering a bad business");
    }
  }

  @Given("I create an illegitimate {string} business {string}")
  public void iCreateAnIllegitimateBusiness(String businessType, String businessName)
      throws Exception {
    CreateBusinessDto business = new CreateBusinessDto();
    FullAddressDto address = new FullAddressDto(makeAddress());
    business
        .setBusinessType(businessType)
        .setName(businessName)
        .setAddress(address);

    response = mockMvc.perform(post("/businesses/")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(business)))
        .andReturn();
  }

  @Then("The business is not created and a {int} error is thrown")
  public void theBusinessIsNotCreatedAndAErrorIsThrown(int errorStatus) {
    if (!(response.getResponse().getStatus() == errorStatus)) {
      Assert.fail("Request should have failed for registering a business with a bad type");
    }
  }

  @Given("I create a legitimate {string} business {string}")
  public void iCreateALegitimateBusiness(String businessType, String businessName)
      throws Exception {
    CreateBusinessDto business = new CreateBusinessDto();
    FullAddressDto address = new FullAddressDto(makeAddress());
    business.setBusinessType(businessType)
        .setName(businessName)
        .setAddress(address)
        .setPrimaryAdministratorId(loggedInUserId);

    jsonResponse = makePostRequestGetJson("/businesses", business, status().isCreated());
  }

  @Then("I can see myself as the primary administrator")
  public void iCanSeeMyselfAsThePrimaryAdministrator() throws Exception {
    // Get the businessId from the returned string object
    businessId = jsonResponse.get("businessId").asLong();

    JsonNode response = makeGetRequestGetJson("/businesses/" + businessId, status().isOk());
    Assertions.assertEquals(loggedInUserId, response.get("primaryAdministratorId").asLong());
    Assertions.assertTrue(response.get("administrators").isArray());
    Assertions.assertEquals(loggedInUserId, response.get("administrators").get(0).get("id").asLong());
  }
}