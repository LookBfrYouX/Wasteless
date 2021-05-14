package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.navbara_pigeons.wasteless.dto.CreateUserDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Inventory;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

public class U19CreateInventoryStepDefs extends CucumberTestProvider {

  private MvcResult mvcResult;
  private User user;
  private int businessId;
  private MvcResult response;

  @Given("these users exist")
  public void theseUsersExist(DataTable dataTable) {
    List<Map<String, String>> rows = dataTable.asMaps(String.class, String.class);
    for (Map<String, String> columns : rows) {
      User newUser = makeUser(columns.get("emailAddress"), "temp", false);
      //password is hashed in makeUser but registerUser hashes the password of user provided already
      newUser.setPassword("TestUserPassword1");
      newUser.setFirstName(columns.get("firstName"));
      System.out.println("CREATED NEW USER: " + newUser.toString());
      Assertions.assertDoesNotThrow(() -> userController.registerUser(new CreateUserDto(newUser)));
      if (newUser.getEmail().equals("Bobby@email.test")) {
        user = newUser;
      }
    }
  }

  @Given("{string} administers a business {string} with an inventory item {string}")
  public void aUserWithNameHasABusinessWithAnInventoryItem(String email, String business, String inventoryItem) {
    Business newBusiness = makeBusiness(business);
    businessId = 123456;
    newBusiness.setId(businessId);
    user.addBusiness(newBusiness);

    Product newProduct = makeProduct(inventoryItem);
    newBusiness.addCatalogueProduct(newProduct);

    Inventory newInventoryItem = makeInventoryItem(newProduct);
    newBusiness.getInventory().add(newInventoryItem);
  }

  @When("{string} is logged in with password {string}")
  public void userIsLoggedIn(String email, String password) throws Exception {
    JSONObject credentials = new JSONObject();
    credentials.put("email", email);
    credentials.put("password", password);
    System.out.println(credentials.toString());

    mockMvc.perform(
        post("/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(credentials.toString())
            .accept(MediaType.ALL))
        .andExpect(status().is(200));
  }

  @When("He tries to access his business called {string} inventory")
  public void userTriesToAccessOwnInventory(String business)
      throws Exception {
    //try to find a way to use business name provided to get business instead of saving business id
    response = mockMvc.perform(
        post("/businesses/{id}/inventory", businessId)
            .content(String.valueOf(businessId)))
        .andExpect(status().is(200)).andReturn();
  }

  @Then("The inventory item {string} is displayed")
  public void theInventoryItemIsDisplayed(String inventoryItem)
      throws UnsupportedEncodingException {
    Assert.assertTrue(response.getResponse().getContentAsString().contains(inventoryItem));
  }

  @When("He tries to access another business called {string} inventory")
  public void aUserWithNameTriesToAccessAnotherBusinessWithAnInventoryItem(String business)
      throws Exception {
    //try to find a way to use business name provided to get business instead of saving business id
    response = mockMvc.perform(
        post("/businesses/{id}/inventory", businessId)
            .content(String.valueOf(businessId)))
        .andExpect(status().is(403)).andReturn();
  }

  @Then("An error is thrown with message {string}")
  public void theProductIsNotDisplayed(String errorMessage) {
    Assert.assertEquals(errorMessage, response.getResponse().getErrorMessage());
  }
}
