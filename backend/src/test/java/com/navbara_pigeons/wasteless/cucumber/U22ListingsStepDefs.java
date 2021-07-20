package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.dto.BasicProductCreationDto;
import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.CreateListingDto;
import com.navbara_pigeons.wasteless.dto.FullAddressDto;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;
import org.springframework.test.web.servlet.MvcResult;

public class U22ListingsStepDefs extends CucumberTestProvider {

  private long businessId;
  private long productId;
  private long inventoryItemId;
  private long listingId;
  private MvcResult responseOne;
  private MvcResult responseTwo;

  // ---------- AC2 ----------
  @Given("a user has a business {string} in {string}")
  public void a_user_has_a_business_in(String businessName, String countryName) throws Exception {
    login();
    CreateBusinessDto business = new CreateBusinessDto();
    business.setBusinessType("Retail Trade");
    business.setName(businessName);
    business.setAddress(new FullAddressDto(makeAddress()));
    business.getAddress().setCountry(countryName);
    business.setPrimaryAdministratorId(loggedInUserId);
    JsonNode json = makePostRequestGetJson("/businesses", business, status().isCreated());
    businessId = json.get("businessId").asLong();
  }

  @And("the business has the product {string} with RRP of {double}")
  public void the_business_has_the_product_with_rrp_of(String name, Double rrp) throws Exception {
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

  @And("my business has {int} of them in stock at {double}")
  public void myBusinessHasOfThemInStockAt(int quantity, double price) throws Exception {
    CreateInventoryItemDto inventoryItem = new CreateInventoryItemDto();
    inventoryItem.setQuantity(quantity);
    inventoryItem.setTotalPrice(price);
    inventoryItem.setProductId(productId);
    inventoryItem.setExpires(LocalDate.now());
    JsonNode response = makePostRequestGetJson(
        "/businesses/" + businessId + "/inventory",
        inventoryItem,
        status().isCreated()
    );

    inventoryItemId = response.get("inventoryItemId").asLong();
  }

  @And("a listing with quantity {int} and price {double} exists")
  public void aListingWithQuantityAndPriceExists(int quantity, Double price) throws Exception {
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(inventoryItemId);
    listing.setQuantity(quantity);
    listing.setPrice(price);

    responseOne = mockMvc.perform(post("/businesses/" + inventoryItemId + "/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andReturn();
  }

  @When("i create another listing with quantity {int} and price {double}")
  public void iCreateAnotherListingWithQuantityAndPrice(int quantity, Double price)
      throws Exception {
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(inventoryItemId);
    listing.setQuantity(quantity);
    listing.setPrice(price);

    responseTwo = mockMvc.perform(post("/businesses/" + inventoryItemId + "/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andReturn();
  }

  @Then("appropriate error messages are shown")
  public void appropriateErrorMessagesAreShown() {
    // First listing should be created, second shouldn't
    Assertions.assertEquals(responseOne.getResponse().getStatus(), 201);
    Assertions.assertEquals(responseTwo.getResponse().getStatus(), 400);
  }


  // ---------- AC4 ----------
  @When("i list {int} of these for sale mentioning {string} as more info on the listing")
  public void iListOfTheseForSaleMentioningAsMoreInfoOnTheListing(int quantity, String moreInfo)
      throws Exception {
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(inventoryItemId);
    listing.setQuantity(quantity);
    listing.setPrice(1.00);
    listing.setMoreInfo(moreInfo);

    mockMvc.perform(post("/businesses/" + inventoryItemId + "/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andExpect(status().is(201));
  }

  @Then("i can see the listing is created with this extra field {string}")
  public void iCanSeeTheListingIsCreatedWithThisExtraField(String moreInfo) throws Exception {
    MvcResult response = mockMvc.perform(get("/businesses/" + businessId + "/listings"))
        .andReturn();
    Assertions.assertTrue(response.getResponse().getContentAsString()
        .contains("\"moreInfo\":" + "\"" + moreInfo + "\""));
  }


  // ---------- AC5 ----------
  @When("i list {int} of these for sale with no closing date supplied")
  public void iListOfTheseForSaleWithNoClosingDateSupplied(int quantity) throws Exception {
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(inventoryItemId);
    listing.setQuantity(quantity);
    listing.setPrice(1.00);

    mockMvc.perform(post("/businesses/" + inventoryItemId + "/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andExpect(status().is(201));
  }

  @Then("i can see the listing is created with the expiry date as the closing date")
  public void iCanSeeTheListingIsCreatedWithTheExpiryDateAsTheClosingDate()
      throws Exception {
    MvcResult response = mockMvc.perform(get("/businesses/" + businessId + "/listings"))
        .andReturn();
    int expiresIndex = response.getResponse().getContentAsString().indexOf("expires");
    int closesIndex = response.getResponse().getContentAsString().indexOf("closes");
    String expires = response.getResponse().getContentAsString()
        .substring(expiresIndex + 10, expiresIndex + 20);
    String closes = response.getResponse().getContentAsString()
        .substring(closesIndex + 9, closesIndex + 19);

    Assertions.assertEquals(expires, closes);
  }
}
