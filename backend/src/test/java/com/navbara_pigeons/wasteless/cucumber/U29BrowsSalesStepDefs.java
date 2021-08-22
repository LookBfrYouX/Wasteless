package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.dto.BasicProductDto;
import com.navbara_pigeons.wasteless.dto.FullListingDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.Listing;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MvcResult;

public class U29BrowsSalesStepDefs extends CucumberTestProvider {

  private MvcResult response;

  @BeforeEach
  void setup() {
    this.response = null;
  }

  @When("I send an empty request to the endpoint {string}")
  public void iSendAnEmptyRequestToTheEndpoint(String endpointUrl) throws Exception {
    response = mockMvc.perform(get(endpointUrl)).andReturn();
    Assertions.assertNotNull(response);
  }

  @When("I send a valid request with no filters to {string}")
  public void iSendAValidRequestWithNoFiltersTo(String endpointUrl) throws Exception {
    response = mockMvc.perform(
        get(endpointUrl)
            .param("pagStartIndex", "0")
            .param("pagEndIndex", "10")
    ).andReturn();
    Assertions.assertNotNull(response);
  }

  @Then("I do not receive a {int} error.")
  public void iDoNotReceiveAError(int statusCode) {
    Assertions.assertNotEquals(this.response.getResponse().getStatus(), statusCode);
  }

  @Then("I receive all the current listings")
  public void iReceiveAllTheCurrentListings()
      throws UnsupportedEncodingException, JsonProcessingException {
    PaginationDto responseDto = objectMapper.readValue(this.response.getResponse().getContentAsString(), PaginationDto.class);
    Assertions.assertTrue(responseDto.getTotalCount() >= 4);
    Assertions.assertNotNull(responseDto.getResults());
  }

  @When("I send a valid request sorted by price to {string}")
  public void iSendAValidRequestSortedByPriceTo(String endpointUrl) throws Exception {
    this.response = mockMvc.perform(
        get(endpointUrl)
            .param("pagStartIndex", "0")
            .param("pagEndIndex", "3")
            .param("sortBy", "price")
            .param("isAscending", "true")
    ).andReturn();
    Assertions.assertNotNull(this.response);
  }

  @Then("The listings are ordered by price")
  public void theFirstListingHasTheLowestPrice()
      throws UnsupportedEncodingException, JsonProcessingException {
    JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
    Iterator<JsonNode> results = jsonResponse.get("results").elements();
    Double minPrice = 0.0;
    while (results.hasNext()) {
      Double listingPrice = results.next().get("price").asDouble();
      Assertions.assertTrue(listingPrice >= minPrice);
      minPrice = listingPrice;
    }
  }

  @When("I send a valid request filtered by business type {string} to {string}")
  public void iSendAValidRequestFilteredByBusinessTypeTo(String businessType, String endpointUrl)
      throws Exception {
    this.response = mockMvc.perform(
        get(endpointUrl)
            .param("pagStartIndex", "0")
            .param("pagEndIndex", "3")
            .param("businessTypes", businessType)
    ).andReturn();
    Assertions.assertNotNull(this.response);
  }

  @Then("Only listings for businesses of type {string} are returned")
  public void onlyListingsForBusinessesOfTypeAreReturned(String businessType)
      throws UnsupportedEncodingException, JsonProcessingException {
    JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
    Iterator<JsonNode> results = jsonResponse.get("results").elements();
    while (results.hasNext()) {
      System.out.println(results.next().get("id"));
    }
  }
}
