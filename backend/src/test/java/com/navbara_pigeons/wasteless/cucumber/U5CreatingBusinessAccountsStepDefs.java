package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.navbara_pigeons.wasteless.dto.CreateUserDto;
import com.navbara_pigeons.wasteless.dto.UserIdDto;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class U5CreatingBusinessAccountsStepDefs extends CucumberTestProvider {

  private MvcResult response;
  private String userId;
  private String newUserId;

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

  // ----- AC1 -----

  @Given("I am logged in with email {string} and password {string}")
  public void iAmLoggedInWithEmailAndPassword(String email, String password) throws Exception {
    JSONObject credentials = new JSONObject();
    credentials.put("email", email);
    credentials.put("password", password);

    MvcResult mvcResult = mockMvc.perform(
        post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(credentials.toString())
            .accept(MediaType.ALL))
        .andExpect(status().is(200)).andReturn();
    userId = mvcResult.getResponse().getContentAsString().replaceAll("[^0-9]", "");
  }

  @When("I create a {string} business {string}")
  public void iCreateABusiness(String businessType, String businessName)
      throws Exception {
    Business business = new Business();
    Address address = makeAddress();
    business.setBusinessType(businessType)
        .setName(businessName)
        .setAddress(address);

    response = mockMvc.perform(post("/businesses/")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(business)))
        .andExpect(status().isCreated()).andReturn();
  }

  @Then("The business {string} is created and stored")
  public void theBusinessIsCreatedAndStored(String businessName) throws Exception {
    // Get the businessId from the returned string object
    String businessId = getBusinessIdFromResponse();

    MvcResult res = mockMvc.perform(get("/businesses/{id}", businessId)
        .content(businessId))
        .andExpect(status().isOk()).andReturn();

    if (!res.getResponse().getContentAsString().contains(businessName)) {
      Assert.fail("Business not found");
    }
  }

  // ----- AC2 -----

  @When("I create a business {string} without the required field: BusinessType")
  public void iCreateABusinessWithoutTheRequiredFieldBusinessType(String businessName)
      throws Exception {
    Business business = new Business();
    Address address = makeAddress();
    business.setName(businessName)
        .setAddress(address);

    response = mockMvc.perform(post("/businesses/")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(business)))
        .andReturn();
  }

  @Then("The business is not created and an error is thrown")
  public void theBusinessIsNotCreatedAndAnErrorIsThrown() {
    if (!(response.getResponse().getStatus() == HttpServletResponse.SC_BAD_REQUEST)) {
      Assert.fail("Request should have failed for registering a bad business");
    }
  }

  // ----- AC3 -----

  @When("I create an illegitimate {string} business {string}")
  public void iCreateAnIllegitimateBusiness(String businessType, String businessName)
      throws Exception {
    Business business = new Business();
    Address address = makeAddress();
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

  // ----- AC5.1 -----

  @When("I create a legitimate {string} business {string}")
  public void iCreateALegitimateBusiness(String businessType, String businessName)
      throws Exception {
    Business business = new Business();
    Address address = makeAddress();
    business.setBusinessType(businessType)
        .setName(businessName)
        .setAddress(address);

    response = mockMvc.perform(post("/businesses/")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(business)))
        .andExpect(status().isCreated()).andReturn();
  }

  @Then("I can see myself as the primary administrator")
  public void iCanSeeMyselfAsThePrimaryAdministrator() throws Exception {
    // Get the businessId from the returned string object
    String businessId = getBusinessIdFromResponse();

    MvcResult res = mockMvc.perform(get("/businesses/{id}", businessId)
        .content(businessId))
        .andExpect(status().isOk()).andReturn();

    // Get the businessId from the returned string object
    if (!res.getResponse().getContentAsString().contains("\"primaryAdministratorId\":" + userId)) {
      Assert.fail("Business not found");
    }
  }

  // ----- AC5.2 -----

  @Given("A user exists with email {string} and password {string}")
  public void aUserExistsWithEmailAndPassword(String email, String password) throws Exception {
    User newUser = makeUser(email, password, false);

    newUserId = userController.registerUser(new CreateUserDto(newUser)).getBody().get("userId")
        .toString();
  }

  @When("I set this user as an admin of my newly created business")
  public void iSetThisUserAsAnAdminOfMyNewlyCreatedBusiness() throws Exception {
    String businessId = getBusinessIdFromResponse();

    UserIdDto userIdDto = new UserIdDto(Long.valueOf(newUserId));

    mockMvc.perform(
        put("/businesses/{id}/makeAdministrator", businessId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userIdDto))
            .accept(MediaType.ALL))
        .andExpect(status().is(200));
  }

  @Then("I can see him in the list of admins for the business")
  public void iCanSeeHimInTheListOfAdminsForTheBusiness() throws Exception {
    // Get the businessId from the returned string object
    String businessId = getBusinessIdFromResponse();

    MvcResult res = mockMvc.perform(get("/businesses/{id}", businessId)
        .content(businessId))
        .andExpect(status().isOk()).andReturn();

    // Check the newUser is in the list of admins for the returned business
    JSONObject jsonResponse = new JSONObject(res.getResponse().getContentAsString());
    Assertions.assertTrue(jsonResponse.get("administrators").toString().contains("\"id\":" + newUserId));
  }

  /**
   * Returns a businessId from a post business endpoint response
   */
  private String getBusinessIdFromResponse() throws UnsupportedEncodingException {
    String businessObj = response.getResponse().getContentAsString();
    return businessObj.replaceAll("[^0-9]", "");
  }

  // ----- AC5.3 -----

  @Then("I can remove him from the list of admins for my business")
  public void iCanRemoveHimFromTheListOfAdminsForMyBusiness() throws Exception {
    String businessId = getBusinessIdFromResponse();
    UserIdDto userIdDto = new UserIdDto(Long.valueOf(newUserId));

    mockMvc.perform(
        put("/businesses/{id}/removeAdministrator", businessId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userIdDto))
            .accept(MediaType.ALL))
        .andExpect(status().is(200));
  }

  @Then("I can see he is not in the list of admins for my business")
  public void iCanSeeHeIsNotInTheListOfAdminsForMyBusiness() throws Exception {
    // Get the businessId from the returned string object
    String businessId = getBusinessIdFromResponse();

    MvcResult res = mockMvc.perform(get("/businesses/{id}", businessId)
        .content(businessId))
        .andExpect(status().isOk()).andReturn();

    // Check the newUser is in the list of admins for the returned business
    JSONObject jsonResponse = new JSONObject(res.getResponse().getContentAsString());
    Assertions.assertFalse(jsonResponse.get("administrators").toString().contains("\"id\":" + newUserId));
  }

  // ----- AC5.4 -----

  @Then("I cannot remove myself from this list of admins")
  public void iCannotRemoveMyselfFromThisListOfAdmins() throws Exception {
    String businessId = getBusinessIdFromResponse();
    UserIdDto userIdDto = new UserIdDto(Long.valueOf(userId));

    mockMvc.perform(
        put("/businesses/{id}/removeAdministrator", businessId)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userIdDto))
            .accept(MediaType.ALL))
        .andExpect(status().is(400));
  }
}