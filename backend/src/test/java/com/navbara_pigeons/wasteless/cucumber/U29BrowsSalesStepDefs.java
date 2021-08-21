package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.UnsupportedEncodingException;
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
    System.out.println(this.response.getResponse().getContentAsString());
    PaginationDto responseDto = objectMapper.readValue(this.response.getResponse().getContentAsString(), PaginationDto.class);
    Assertions.assertTrue(responseDto.getTotalCount() >= 4);
    Assertions.assertNotNull(responseDto.getResults());
  }

}
