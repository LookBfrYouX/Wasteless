package com.navbara_pigeons.wasteless.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.dto.CreateProductDto;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.enums.NutritionFactsLevel;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;

import java.io.UnsupportedEncodingException;
import java.util.Iterator;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

public class EU3SearchListingsByNutritionInfoStepDefs extends CucumberTestProvider{
    private MvcResult response;


    @BeforeEach
    void setup() {
        this.response = null;
    }

    @When("I send a valid request to {string} with fat set to {string}")
    public void iSendAValidRequestToWithFatSetTo(String endpointUrl, String fatValue) throws Exception {
        this.response = mockMvc.perform(
                get(endpointUrl)
                        .param("fat", fatValue)
        ).andReturn();
        Assertions.assertNotNull(this.response);
    }

    @Then("only the products with fat set to {string} are shown")
    public void onlyTheProductsWithFatSetToAreShown(String fatValue) throws UnsupportedEncodingException, JsonProcessingException {
        JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
        Iterator<JsonNode> results = jsonResponse.get("results").elements();
        while (results.hasNext()) {
            Assertions.assertTrue(results.next().get("inventoryItem").get("product").get("fat").asText().equalsIgnoreCase(fatValue));
        }
    }

    @When("I send a valid request to {string} with saturatedFat set to {string} and {string}")
    public void iSendAValidRequestToWithSaturatedFatSetToAnd(String endpointUrl, String saturatedFatValue1, String saturatedFatValue2) throws Exception {
        this.response = mockMvc.perform(
                get(endpointUrl)
                        .param("pagStartIndex", "0")
                        .param("pagEndIndex", "3")
                        .param("saturatedFat", saturatedFatValue1)
                        .param("saturatedFat", saturatedFatValue2)
        ).andReturn();
        Assertions.assertNotNull(this.response);
    }

    @Then("only the products with saturatedFat set to {string} and {string} are shown")
    public void onlyTheProductsWithSaturatedFatSetToAndAreShown(String saturatedFatValue1, String saturatedFatValue2) throws UnsupportedEncodingException, JsonProcessingException {
        JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
        Iterator<JsonNode> results = jsonResponse.get("results").elements();
        while (results.hasNext()) {
            String text = results.next().get("inventoryItem").get("product").get("saturatedFat").asText();
            Assertions.assertTrue(text.equalsIgnoreCase(saturatedFatValue1) || text.equalsIgnoreCase(saturatedFatValue2));
        }
    }

    @When("I send a valid request to {string} with salt set to {string}")
    public void iSendAValidRequestToWithSaltSetTo(String endpointUrl, String saltValue) throws Exception {
        this.response = mockMvc.perform(
                get(endpointUrl)
                        .param("pagStartIndex", "0")
                        .param("pagEndIndex", "3")
                        .param("salt", saltValue)
        ).andReturn();
        Assertions.assertNotNull(this.response);
    }

    @Then("only the products with salt set to {string} are shown")
    public void onlyTheProductsWithSaltSetToAreShown(String saltValue) throws UnsupportedEncodingException, JsonProcessingException {
        JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
        Iterator<JsonNode> results = jsonResponse.get("results").elements();
        while (results.hasNext()) {
            Assertions.assertTrue(results.next().get("inventoryItem").get("product").get("salt").asText().equalsIgnoreCase(saltValue));
        }
    }


    @When("I send a valid request to {string} with  sugars set to {string}")
    public void iSendAValidRequestToWithSugarsSetTo(String endpointUrl, String sugarsValue) throws Exception {
        this.response = mockMvc.perform(
                get(endpointUrl)
                        .param("pagStartIndex", "0")
                        .param("pagEndIndex", "3")
                        .param("sugars", sugarsValue)
        ).andReturn();
        Assertions.assertNotNull(this.response);
    }

    @Then("only the products with sugars set to {string} are shown")
    public void onlyTheProductsWithSugarsSetToAreShown(String sugarsValue) throws UnsupportedEncodingException, JsonProcessingException {
        JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
        Iterator<JsonNode> results = jsonResponse.get("results").elements();
        while (results.hasNext()) {
            Assertions.assertTrue(results.next().get("inventoryItem").get("product").get("sugars").asText().equalsIgnoreCase(sugarsValue));
        }
    }
}
