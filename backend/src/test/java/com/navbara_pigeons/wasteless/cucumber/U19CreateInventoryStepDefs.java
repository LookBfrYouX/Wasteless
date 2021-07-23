package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.navbara_pigeons.wasteless.dto.CreateUserDto;
import com.navbara_pigeons.wasteless.entity.User;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class U19CreateInventoryStepDefs extends CucumberTestProvider {

  private User user;
  private String businessId;
  private MvcResult response;

  @Given("these users all exist")
  public void theseUsersExist(DataTable dataTable) {
    List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> columns : rows) {
      User newUser = makeUser(columns.get("emailAddress"), "temp", false);
      //password is hashed in makeUser but registerUser hashes the password of user provided already
      newUser.setPassword(columns.get("password"));
      newUser.setLastName("LastName");
      newUser.setNickname("NickName");
      newUser.setFirstName(columns.get("firstName"));
      System.out.println("CREATED NEW USER: " + newUser.toString());
      Assertions.assertDoesNotThrow(() -> userController.registerUser(new CreateUserDto(newUser)));
      if (newUser.getEmail().equals("Bobby@email.test")) {
        user = newUser;
      }
    }
  }

  @Given("{string} administers a business {string} with an inventory item {string}")
  public void aUserWithNameHasABusinessWithAnInventoryItem(String email, String business,
      String inventoryItem)
      throws Exception {
    //cant get to work
  }

  @When("{string} is logged in with password {string}")
  public void userIsLoggedIn(String email, String password) throws Exception {
    login(email, password);
  }

  @When("He tries to access his business called {string} inventory")
  public void userTriesToAccessOwnInventory(String business)
      throws Exception {
    //try to find a way to use business name provided to get business instead of saving business id
    System.out.println(user.getBusinesses());
    response = mockMvc.perform(
        get("/businesses/{id}/inventory", businessId)
            .content(businessId))
        .andExpect(status().is(200)).andReturn();
  }

  @Then("The inventory item {string} is displayed")
  public void theInventoryItemIsDisplayed(String inventoryItem)
      throws UnsupportedEncodingException {
    System.out.println(response.getResponse().getContentAsString());
    Assert.assertTrue(response.getResponse().getContentAsString().contains(inventoryItem));
  }

  @When("He tries to access another business called {string} inventory")
  public void aUserWithNameTriesToAccessAnotherBusinessWithAnInventoryItem(String business)
      throws Exception {
    //try to find a way to use business name provided to get business instead of saving business id
    response = mockMvc.perform(
        get("/businesses/{id}/inventory", businessId)
            .content(businessId))
        .andExpect(status().is(403)).andReturn();
  }

  @Then("An error is thrown with message {string}")
  public void theProductIsNotDisplayed(String errorMessage) {
    Assert.assertEquals(errorMessage, response.getResponse().getErrorMessage());
  }
}
