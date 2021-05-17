package com.navbara_pigeons.wasteless.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.dto.BasicProductCreationDto;
import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.dto.FullAddressDto;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class U22ListingsStepDefs extends CucumberTestProvider {
  private long businessId;
  private long productId;

  @Given("a user has a business {string} in {string}")
  public void a_user_has_a_business_in(String businessName, String countryName) throws Exception {
    // Write code here that turns the phrase above into concrete actions
    login();
    CreateBusinessDto business = new CreateBusinessDto();
    business.setBusinessType("Retail Trade");
    business.setName(businessName);
    business.setAddress(new FullAddressDto(makeAddress()));
    business.getAddress().setCountry(countryName);

    JsonNode json = makePostRequestGetJson("/businesses", business, status().isCreated());
    businessId = json.get("businessId").asLong();
  }

  @Given("the business has the product {string} with RRP of {double}")
  public void the_business_has_the_product_with_rrp_of(String name, Double rrp) throws Exception {
    // Write code here that turns the phrase above into concrete actions
    BasicProductCreationDto product = new BasicProductCreationDto();
    product.setName(name);
    product.setManufacturer("Should be optional but might still be required");
    product.setRecommendedRetailPrice(rrp);
    JsonNode response = makePostRequestGetJson(
        "/businesses/" + businessId + "/products",
        product,
        status().isCreated()
    );

    productId = response.get("productId").asLong();
  }

//
//  @Given("they have {int} in their inventory with a price of {double} expiring on {string}")
//  public void they_have_in_their_inventory_with_a_price_of_expiring_on(Integer int1, Double double1, String string) {
//    // Write code here that turns the phrase above into concrete actions
//    throw new io.cucumber.java.PendingException();
//  }
//
//  @Given("a listing with quantity {int} and price {double}")
//  public void a_listing_with_quantity_and_price(Integer int1, Double double1) {
//    // Write code here that turns the phrase above into concrete actions
//    throw new io.cucumber.java.PendingException();
//  }
//
//  @Then("another listing with quantity {int} and price {double} succeeds")
//  public void another_listing_with_quantity_and_price_succeeds(Integer int1, Double double1) {
//    // Write code here that turns the phrase above into concrete actions
//    throw new io.cucumber.java.PendingException();
//  }
}
