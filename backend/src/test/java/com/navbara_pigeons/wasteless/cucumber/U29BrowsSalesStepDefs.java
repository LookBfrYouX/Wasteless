package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.UnsupportedEncodingException;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.MvcResult;

public class U29BrowsSalesStepDefs extends CucumberTestProvider {

  private MvcResult response;

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

  @Then("I recieve all the current listings")
  public void iRecieveAllTheCurrentListings()
      throws UnsupportedEncodingException, JsonProcessingException {
    PaginationDto responseDto = objectMapper.readValue(this.response.getResponse().getContentAsString(), PaginationDto.class);
    Assertions.assertEquals(4, responseDto.getTotalCount());
    Assertions.assertNotNull(responseDto.getResults());
  }

}
