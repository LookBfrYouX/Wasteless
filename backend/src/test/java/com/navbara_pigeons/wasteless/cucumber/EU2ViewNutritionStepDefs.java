package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.cucumber.CucumberTestProvider;
import com.navbara_pigeons.wasteless.dto.BasicProductDto;
import com.navbara_pigeons.wasteless.dto.CreateProductDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.enums.NutritionFactsLevel;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import org.junit.Assert;
import org.junit.jupiter.api.Assertions;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

public class EU2ViewNutritionStepDefs extends CucumberTestProvider {

  private MvcResult response;

  private Long id;
  private JsonNode testProduct;

  @Given("There is a product {string} with a {string} level of fat and a {string} level of sugar")
  public void thereIsAProductWithALevelOfFatAndALevelOfSugar(String productName, String fatLevel,
      String sugarLevel) throws Exception {
    adminLogin();
    Product product = makeProduct(productName);
    CreateProductDto productDto = new CreateProductDto(product);
    productDto.setFat(NutritionFactsLevel.valueOf(fatLevel));
    productDto.setSugar(NutritionFactsLevel.valueOf(sugarLevel));
    this.response = mockMvc.perform(post("/businesses/1001/products")
        .contentType(MediaType.APPLICATION_JSON)
        .content(objectMapper.writeValueAsString(productDto))).andReturn();
    Assertions.assertNotNull(this.response);
    JsonNode responseJson = objectMapper.readTree(this.response.getResponse().getContentAsString());
    this.id = responseJson.get("productId").asLong();
    Assertions.assertNotNull(this.id);
  }

  @When("I request the product details")
  public void iRequestTheProductDetails() throws Exception {
    this.response = mockMvc.perform(get("/businesses/1001/products")).andReturn();
    JsonNode jsonResponse = objectMapper.readTree(this.response.getResponse().getContentAsString());
    Iterator<JsonNode> results = jsonResponse.get("results").elements();
    while (results.hasNext()) {
      JsonNode currentProduct = results.next();
      if (currentProduct.get("id").asLong() == this.id) {
        this.testProduct = currentProduct;
      }
    }
    Assertions.assertNotNull(this.testProduct);
  }

  @Then("I can see that the level of fat is {string} and the level of sugar is {string}")
  public void iCanSeeThatTheLevelOfFatIsAndTheLevelOfSugarIs(String fatLevel, String sugarLevel)
      throws UnsupportedEncodingException, JsonProcessingException {
    Assertions.assertEquals(this.testProduct.get("fat").asText(), fatLevel);
    Assertions.assertEquals(this.testProduct.get("sugar").asText(), sugarLevel);
  }
}
