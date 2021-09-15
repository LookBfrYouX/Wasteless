package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.MvcResult;

public class EU3SearchListingsByNutritionInfoStepDefs extends CucumberTestProvider {

  private MvcResult mvcResult;

  @Given("a user is logged in")
  public void aUserIsLoggedIn() throws Exception {
    nonAdminLogin();
  }

  @When("I send a request to {string} with max NOVA group set to {int}")
  public void iSendARequestToWithMaxNOVAGroupSetTo(String endpoint, int novaGroup)
      throws Exception {
    this.mvcResult = mockMvc.perform(get(endpoint)
        .queryParam("maxNovaGroup", String.valueOf(novaGroup)))
        .andExpect(status().is(200))
        .andReturn();
  }

  @Then("only listings with a NOVA group of {int} and {int} are received")
  public void onlyListingsWithANOVAGroupOfAndAreReceived(int novaGroup1, int novaGroup2)
      throws Exception {
    for (JsonNode result : objectMapper.readTree(this.mvcResult.getResponse().getContentAsString())
        .get("results")) {
      int novaGroup = result.get("inventoryItem").get("product").get("novaGroup").asInt();
      if (novaGroup != novaGroup1 && novaGroup != novaGroup2) {
        Assertions.fail();
      }
    }
  }

  @When("I send a request to {string} with min NOVA group set to {int}")
  public void iSendARequestToWithMinNOVAGroupSetTo(String endpoint, int novaGroup)
      throws Exception {
    this.mvcResult = mockMvc.perform(get(endpoint)
        .queryParam("minNovaGroup", String.valueOf(novaGroup)))
        .andExpect(status().is(200))
        .andReturn();
  }

  @When("I send a request to {string} with min NOVA group set to {int} and max nova group set to {int}")
  public void iSendARequestToWithMinNOVAGroupSetToAndMaxNovaGroupSetTo(String endpoint,
      int novaGroupMin,
      int novaGroupMax) throws Exception {
    this.mvcResult = mockMvc.perform(get(endpoint)
        .queryParam("minNovaGroup", String.valueOf(novaGroupMin))
        .queryParam("maxNovaGroup", String.valueOf(novaGroupMax)))
        .andExpect(status().is(200))
        .andReturn();
  }
}