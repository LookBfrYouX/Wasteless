package com.navbara_pigeons.wasteless.cucumber;

import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.dto.BasicProductCreationDto;
import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.CreateListingDto;
import com.navbara_pigeons.wasteless.dto.FullAddressDto;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Listing;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class U22ListingsStepDefs extends CucumberTestProvider {

  private long businessId;
  private long productId;
  private long inventoryItemId;
  private MvcResult responseOne;
  private MvcResult responseTwo;

  // ---------- AC2 ----------
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

  @And("the business has the product {string} with RRP of {double}")
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

    System.out.println("HERERER:" + response);
    productId = response.get("productId").asLong();
  }

  @And("my business has {int} of them in stock at {double}")
  public void myBusinessHasOfThemInStockAt(int quantity, double price) throws Exception {
    // Write code here that turns the phrase above into concrete actions
    CreateInventoryItemDto inventoryItem = new CreateInventoryItemDto();
    inventoryItem.setQuantity(quantity);
    inventoryItem.setTotalPrice(price);
    inventoryItem.setProductId(productId);
    JsonNode response = makePostRequestGetJson(
        "/businesses/" + businessId + "/inventory",
        inventoryItem,
        status().isCreated()
    );

    inventoryItemId = response.get("inventoryItemId").asLong();
  }

  @And("a listing with quantity {int} and price {double} exists")
  public void aListingWithQuantityAndPriceExists(int quantity, int price) throws Exception {
    // Create listing
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(inventoryItemId);
    listing.setQuantity(quantity);
    listing.setPrice(price);

    responseOne = mockMvc.perform(post("/businesses/")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andReturn();
  }

  @When("i create another listing with quantity {int} and price {double}")
  public void iCreateAnotherListingWithQuantityAndPrice(int quantity, int price) throws Exception {
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(inventoryItemId);
    listing.setQuantity(quantity);
    listing.setPrice(price);

    responseTwo = mockMvc.perform(post("/businesses/")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andReturn();
  }

  @Then("appropriate error messages are shown")
  public void appropriateErrorMessagesAreShown() {
    System.out.println(responseOne);
    System.out.println(responseTwo);
    // TODO: Assert these responses contain errors
    Assertions.fail();
  }


  // ---------- AC3 ----------
  @When("i list {int} of these for sale")
  public void iListOfTheseForSale(int quantity) throws Exception {
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(inventoryItemId);
    listing.setQuantity(quantity);
    listing.setPrice(0.00F);

    responseOne = mockMvc.perform(post("/businesses/")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andReturn();
  }

  @Then("i can see the price is generated by the inventory item price * the quantity in the listing")
  public void iCanSeeThePriceIsGeneratedByTheInventoryItemPriceTheQuantityInTheListing() {
    System.out.println(responseOne);
    // TODO: Check the total price = price per item * quantity
    Assertions.fail();
  }


  // ---------- AC4 ----------
  @When("i list {int} of these for sale mentioning {string} as more info on the listing")
  public void iListOfTheseForSaleMentioningAsMoreInfoOnTheListing(int quantity, String moreInfo) throws Exception {
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(inventoryItemId);
    listing.setQuantity(quantity);
    listing.setPrice(0.00F);
    listing.setMoreInfo(moreInfo);

    responseOne = mockMvc.perform(post("/businesses/")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andReturn();
  }

  @Then("i can see the listing is created with this extra field")
  public void iCanSeeTheListingIsCreatedWithThisExtraField() {
    System.out.println(responseOne);
    // TODO: Check more info is not null
    Assertions.fail();
  }


  // ---------- AC5 ----------
  @When("i list {int} of these for sale with no closing date supplied")
  public void iListOfTheseForSaleWithNoClosingDateSupplied(int quantity) throws Exception {
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(inventoryItemId);
    listing.setQuantity(quantity);
    listing.setPrice(0.00F);

    responseOne = mockMvc.perform(post("/businesses/")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andReturn();
  }

  @Then("i can see the listing is created with the expiry date as the closing date")
  public void iCanSeeTheListingIsCreatedWithTheExpiryDateAsTheClosingDate() {
    System.out.println(responseOne);
    // TODO: Check closing date = expiration date
    Assertions.fail();
  }
}
