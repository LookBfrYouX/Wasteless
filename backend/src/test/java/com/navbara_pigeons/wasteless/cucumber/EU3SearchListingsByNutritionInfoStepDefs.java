package com.navbara_pigeons.wasteless.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

public class EU3SearchListingsByNutritionInfoStepDefs extends CucumberTestProvider{
    private MvcResult response;

    @BeforeEach
    void setup() {
        this.response = null;
    }

    @When("I send a valid request to {string} with fat set to {string}")
    void i_send_a_valid_request_to_with_fat_set_to(String endPointUrl, String fatValue) throws Exception {
        this.response = mockMvc.perform(
                get(endPointUrl)
                        .param("fat", fatValue)
        ).andReturn();
        Assertions.assertNotNull(this.response);
    }

    @Then("only the products with fat set to {string} are shown")
    void only_the_products_with_fat_set_to_are_shown(String fatValue) throws UnsupportedEncodingException, JsonProcessingException {
        JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
        Iterator<JsonNode> results = jsonResponse.get("results").elements();
        while (results.hasNext()) {
            Assertions.assertTrue(results.next().get("fat").asText().equalsIgnoreCase(fatValue));
        }
    }

    @When("I send a valid request to {string} with saturatedFat set to {string} and {string}")
    void i_send_a_valid_request_to_with_saturated_fat_set_to_and(String endPointUrl, String saturatedFatValue1, String saturatedFatValue2) throws Exception {
        this.response = mockMvc.perform(
                get(endPointUrl)
                        .param("pagStartIndex", "0")
                        .param("pagEndIndex", "3")
                        .param("saturatedFat", saturatedFatValue1)
                        .param("saturatedFat", saturatedFatValue2)
        ).andReturn();
        Assertions.assertNotNull(this.response);
    }

    @Then("only the products with saturatedFat set to {string} and {string} are shown")
    void only_the_products_with_saturated_fat_set_to_and_are_shown(String saturatedFatValue1, String saturatedFatValue2) throws UnsupportedEncodingException, JsonProcessingException {
        JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
        Iterator<JsonNode> results = jsonResponse.get("results").elements();
        while (results.hasNext()) {
            Assertions.assertTrue(results.next().get("saturatedFat").asText().equalsIgnoreCase(saturatedFatValue1) || results.next().get("saturatedFat").asText().equalsIgnoreCase(saturatedFatValue2));
        }
    }

    @When("I send a valid request to {string} with salt set to {string}")
    void i_send_a_valid_request_to_with_salt_set_to(String endPointUrl, String saltValue) throws Exception {
        this.response = mockMvc.perform(
                get(endPointUrl)
                        .param("pagStartIndex", "0")
                        .param("pagEndIndex", "3")
                        .param("salt", saltValue)
        ).andReturn();
        Assertions.assertNotNull(this.response);
    }

    @Then("only the products with salt set to {string} are shown")
    void onlyTheProductsWithSaltSetToAreShown(String saltValue) throws UnsupportedEncodingException, JsonProcessingException {
        JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
        Iterator<JsonNode> results = jsonResponse.get("results").elements();
        while (results.hasNext()) {
            Assertions.assertTrue(results.next().get("salt").asText().equalsIgnoreCase(saltValue));
        }
    }

    @When("I send a valid request to {string} with  sugars set to {string}")
    void i_send_a_valid_request_to_with_sugars_set_to(String endPointUrl, String sugarsValue) throws Exception {
        this.response = mockMvc.perform(
                get(endPointUrl)
                        .param("pagStartIndex", "0")
                        .param("pagEndIndex", "3")
                        .param("sugars", sugarsValue)
        ).andReturn();
        Assertions.assertNotNull(this.response);
    }

    @Then("only the products with sugars set to {string} are shown")
    void only_the_products_with_sugars_set_to_are_shown(String sugarsValue) throws UnsupportedEncodingException, JsonProcessingException {
        JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
        Iterator<JsonNode> results = jsonResponse.get("results").elements();
        while (results.hasNext()) {
            Assertions.assertTrue(results.next().get("sugars").asText().equalsIgnoreCase(sugarsValue));
        }
    }
}
