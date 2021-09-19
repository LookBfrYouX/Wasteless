package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.enums.NutriScore;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;

public class EU3SearchListingsByNutritionInfoStepDefs extends CucumberTestProvider {

  private MvcResult mvcResult;

  @When("I send a valid request to {string} with vegan set to {string}")
  public void iSendAValidRequestToWithVeganSetTo(String endpoint, String veganValue) throws Exception {
    this.mvcResult = mockMvc.perform(get(endpoint)
                    .queryParam("isVegan", "true"))
            .andExpect(status().is(200))
            .andReturn();
  }

  @Then("only the products that are vegan are shown")
  public void onlyTheProductsThatAreVeganAreShown() throws UnsupportedEncodingException, JsonProcessingException {
    for (JsonNode result : objectMapper.readTree(this.mvcResult.getResponse().getContentAsString())
            .get("results")) {
      Assertions.assertTrue(Boolean.parseBoolean(result.get("inventoryItem").get("product").get("isVegan").asText()));
    }
  }

  @When("I send a valid request to {string} with vegan set to {string} and gluten free set to {string}")
  public void iSendAValidRequestToWithVeganSetToAndGlutenFreeSetTo(String endpoint, String veganValue, String glutenFreeValue) throws Exception {
    this.mvcResult = mockMvc.perform(get(endpoint)
                    .queryParam("isVegan", "true")
                    .queryParam("isGlutenFree", "true"))
            .andExpect(status().is(200))
            .andReturn();
  }

  @Then("only the products that are vegan and gluten free are shown")
  public void onlyTheProductsThatAreVeganAndGlutenFreeAreShown() throws UnsupportedEncodingException, JsonProcessingException {
    for (JsonNode result : objectMapper.readTree(this.mvcResult.getResponse().getContentAsString())
            .get("results")) {
      Assertions.assertTrue(Boolean.parseBoolean(result.get("inventoryItem").get("product").get("isVegan").asText()));
      Assertions.assertTrue(Boolean.parseBoolean(result.get("inventoryItem").get("product").get("isGlutenFree").asText()));
    }
  }

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

  @When("I send a request to {string} with max nutri-score set to {string}")
  public void i_send_a_request_to_with_max_nutri_score_set_to_b(String endpoint, String nutriScore)
      throws Exception {
    this.mvcResult = mockMvc.perform(get(endpoint)
        .queryParam("maxNutriScore", nutriScore))
        .andExpect(status().is(200))
        .andReturn();
  }

  @Then("only listings with a nutri-score of {string} and {string} are received")
  public void only_listings_with_a_nutri_score_of_and_are_received(String nutriScore1,
      String nutriScore2)
      throws Exception {
    for (JsonNode result : objectMapper.readTree(this.mvcResult.getResponse().getContentAsString())
        .get("results")) {
      NutriScore nutriScore = NutriScore
          .valueOf(result.get("inventoryItem").get("product").get("nutriScore").asText());
      if (!nutriScore.toString().equals(nutriScore1) && !nutriScore.toString()
          .equals(nutriScore2)) {
        Assertions.fail();
      }
    }
  }

  @When("I send a request to {string} with min nutri-score group set to {string}")
  public void i_send_a_request_to_with_min_nutri_score_group_set_to_d(String endpoint,
      String nutriScore) throws Exception {
    this.mvcResult = mockMvc.perform(get(endpoint)
        .queryParam("minNutriScore", nutriScore))
        .andExpect(status().is(200))
        .andReturn();
  }

  @When("I send a request to {string} with min nutri-score set to {string} and max nutri-score set to {string}")
  public void i_send_a_request_to_with_min_nutri_score_set_to_b_and_max_nutri_score_set_to_d(
      String endpoint,
      String minNutriScore,
      String maxNutriScore) throws Exception {
    this.mvcResult = mockMvc.perform(get(endpoint)
        .queryParam("minNutriScore", minNutriScore)
        .queryParam("maxNutriScore", maxNutriScore))
        .andExpect(status().is(200))
        .andReturn();
  }

  @Then("only listings with a NOVA group of {string}, {string} and {string} are received")
  public void only_listings_with_a_nova_group_of_and_are_received(String nutriScore1,
      String nutriScore2, String nutriScore3) throws Exception {
    for (JsonNode result : objectMapper.readTree(this.mvcResult.getResponse().getContentAsString())
        .get("results")) {
      NutriScore nutriScore = NutriScore
          .valueOf(result.get("inventoryItem").get("product").get("nutriScore").asText());
      if (!nutriScore.toString().equals(nutriScore1) &&
          !nutriScore.toString().equals(nutriScore2) &&
          !nutriScore.toString().equals(nutriScore3)
      ) {
        Assertions.fail();
      }
    }
  }
}