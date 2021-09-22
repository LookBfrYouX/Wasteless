package com.navbara_pigeons.wasteless.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.request;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class U41BusinessTransactionHistory extends CucumberTestProvider {
  Long businessId = null;
  JsonNode response = null;

  @Given("a user with username {string} and password {string} is logged in")
  public void a_user_with_username_and_password_is_logged_in(String email, String password) throws Exception {
    login(email, password);
  }

  @Given("Given they are managing a business called {string}")
  public void given_they_are_managing_a_business_called(String businessName) throws Exception {
    // Write code here that turns the phrase above into concrete actions
    JsonNode request = makeGetRequestGetJson("/users/" + loggedInUserId, status().isOk());
    for(JsonNode business: request.get("businessesAdministered")) {
      if (businessName.equals(business.get("name").asText())) {
        businessId = business.get("id").asLong();
        return;
      }
    }

    if (businessId == null) {
      throw new Exception("Could not find business with the right name");
    }
  }

  @When(
      "I send a request to the transaction endpoint with a start date of {string}, end date of {string} and granularity of {string}")
  public void
      i_send_a_request_to_the_transaction_endpoint_with_a_start_date_of_end_date_of_and_granularity_of(
          String startDate, String endDate, String granularity) throws Exception {
    MockHttpServletRequestBuilder requestBuilder = get("/businesses/" + businessId + "/transactions");
    if (!startDate.isEmpty()) requestBuilder = requestBuilder.param("startDate", startDate);
    if (!endDate.isEmpty()) requestBuilder = requestBuilder.param("endDate", endDate);
    if (!granularity.isEmpty()) requestBuilder = requestBuilder.param("granularity", granularity);
    ResultActions result = mockMvc.perform(requestBuilder);
    String response = result.andReturn().getResponse().getContentAsString();
    this.response = objectMapper.readTree(response);
  }

  @Then("The returned dates are between {string} and {string} inclusive")
  public void the_returned_dates_are_between_and_inclusive(String string, String string2) {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }

  @Then("There is only one entry per month")
  public void there_is_only_one_entry_per_month() {
    // Write code here that turns the phrase above into concrete actions
    throw new io.cucumber.java.PendingException();
  }
}