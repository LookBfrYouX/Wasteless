package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.MvcResult;

public class U29BrowsSalesStepDefs extends CucumberTestProvider {

  private MvcResult response;

  @When("I send a request to the endpoint {string}")
  public void iSendARequestToTheEndpoint(String endpointUrl) throws Exception {
    this.response = mockMvc.perform(get(endpointUrl)).andReturn();
    Assertions.assertNotNull(this.response);
  }

  @Then("I do not receive a {int} error.")
  public void iDoNotReceiveAError(int statusCode) {
    Assertions.assertNotEquals(this.response.getResponse().getStatus(), statusCode);
  }
}
