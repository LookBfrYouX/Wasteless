package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Iterator;
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
            .param("sortBy", "PRICE")
            .param("isAscending", "true")
    ).andReturn();
    Assertions.assertNotNull(this.response);
  }

  @Then("The listings are ordered by price")
  public void theFirstListingHasTheLowestPrice()
      throws UnsupportedEncodingException, JsonProcessingException {
    JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
    Iterator<JsonNode> results = jsonResponse.get("results").elements();
    double minPrice = 0.0;
    while (results.hasNext()) {
      double listingPrice = results.next().get("price").asDouble();
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
      Assertions.assertTrue(results.next().get("inventoryItem").get("business").get("businessType").asText().equalsIgnoreCase(businessType));
    }
  }

  @When("I send a valid request searching for listings with products containing {string} to {string}")
  public void iSendAValidRequestSearchingForListingsWithProductsContainingTo(String searchParam, String endpointUrl)
      throws Exception {
    this.response = mockMvc.perform(
        get(endpointUrl)
            .param("pagStartIndex", "0")
            .param("pagEndIndex", "3")
            .param("searchKeys", "PRODUCT_NAME")
            .param("searchParam", searchParam)
    ).andReturn();
    Assertions.assertNotNull(this.response);
  }

  @Then("Listings with products containing the word {string} are shown")
  public void listingsWithProductsContainingTheWordAreShown(String searchParam)
      throws UnsupportedEncodingException, JsonProcessingException {
    JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
    Iterator<JsonNode> results = jsonResponse.get("results").elements();
    while (results.hasNext()) {
      Assertions.assertTrue(results.next().get("inventoryItem").get("product").get("name").asText().contains(searchParam));
    }
  }

  @When("I send a valid request with a price range of {double} to {double} to {string}")
  public void iSendAValidRequestWithAPriceRangeOfToTo(double minPrice, double maxPrice, String endpointUrl)
      throws Exception {
    this.response = mockMvc.perform(
        get(endpointUrl)
            .param("pagStartIndex", "0")
            .param("pagEndIndex", "3")
            .param("minPrice", Double.toString(minPrice))
            .param("maxPrice", Double.toString(maxPrice))
    ).andReturn();
    Assertions.assertNotNull(this.response);
  }

  @Then("I am only shown listings more than {double} and less than {double}")
  public void iAmOnlyShownListingsMoreThanAndLessThan(double minPrice, double maxPrice)
      throws UnsupportedEncodingException, JsonProcessingException {
    JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
    Iterator<JsonNode> results = jsonResponse.get("results").elements();
    while (results.hasNext()) {
      JsonNode priceNode = results.next().get("price");
      Assertions.assertTrue(Double.parseDouble(priceNode.asText()) >= minPrice);
      Assertions.assertTrue(Double.parseDouble(priceNode.asText()) <= maxPrice);
    }
  }

  @When("I send a valid request searching for listings with products offered by {string} to {string}")
  public void iSendAValidRequestSearchingForListingsWithProductsOfferedByTo(String businessName,
      String endpointUrl) throws Exception {
    this.response = mockMvc.perform(
        get(endpointUrl)
            .param("pagStartIndex", "0")
            .param("pagEndIndex", "3")
            .param("searchKeys", "BUSINESS_NAME")
            .param("searchParam", businessName)
    ).andReturn();
    Assertions.assertNotNull(this.response);
  }

  @Then("Listings from sellers called {string} are shown")
  public void listingsFromSellersCalledAreShown(String businessName)
      throws UnsupportedEncodingException, JsonProcessingException {
    JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
    Iterator<JsonNode> results = jsonResponse.get("results").elements();
    while (results.hasNext()) {
      Assertions.assertTrue(results.next().get("inventoryItem").get("business").get("name").asText().contains(businessName));
    }
  }

  @When("I send a valid request searching for listings with business located in {string} to {string}")
  public void iSendAValidRequestSearchingForListingsWithBusinessLocatedInTo(String address,
      String endpointUrl) throws Exception {
    this.response = mockMvc.perform(
        get(endpointUrl)
            .param("pagStartIndex", "0")
            .param("pagEndIndex", "3")
            .param("searchKeys", "ADDRESS")
            .param("searchParam", address)
    ).andReturn();
    Assertions.assertNotNull(this.response);
  }

  @Then("Listings from sellers located in {string} are shown")
  public void listingsFromSellersLocatedInAreShown(String address)
      throws UnsupportedEncodingException, JsonProcessingException {
    JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
    Iterator<JsonNode> results = jsonResponse.get("results").elements();
    while (results.hasNext()) {
      JsonNode addressNode = results.next().get("inventoryItem").get("business").get("address");
      Assertions.assertTrue(addressNode.get("city").asText().contains(address) || addressNode.get("country").asText().contains(address));
    }
  }

  @When("I send a valid request searching for listings closing between now and in three months to {string}")
  public void iSendAValidRequestSearchingForListingsCreatedBetweenThreeMonthAgoAndNowTo(String endpointUrl)
      throws Exception {
    ZonedDateTime oneMonthAgo = ZonedDateTime.now().minusMonths(3);
    ZonedDateTime now = ZonedDateTime.now();
    this.response = mockMvc.perform(
        get(endpointUrl)
            .param("pagStartIndex", "0")
            .param("pagEndIndex", "3")
            .param("filterDates", oneMonthAgo.toString())
            .param("filterDates", now.toString())
    ).andReturn();
    Assertions.assertNotNull(this.response);
  }

  @Then("Listings closing in the next three months are shown")
  public void listingsCreatedInTheLastMonthAreShown()
      throws UnsupportedEncodingException, JsonProcessingException {
    JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
    Iterator<JsonNode> results = jsonResponse.get("results").elements();
    while (results.hasNext()) {
      JsonNode timeNode = results.next().get("closes");
      Assertions.assertTrue(
          ZonedDateTime.parse(timeNode.asText()).isBefore(ZonedDateTime.now()) &&
              ZonedDateTime.parse(timeNode.asText()).isAfter(ZonedDateTime.now().minusMonths(3)));
    }
  }
}
