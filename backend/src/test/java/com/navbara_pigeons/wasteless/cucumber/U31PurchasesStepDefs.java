package com.navbara_pigeons.wasteless.cucumber;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.CreateListingDto;
import com.navbara_pigeons.wasteless.dto.CreateProductDto;
import com.navbara_pigeons.wasteless.dto.FullAddressDto;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.exception.ListingNotFoundException;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import org.junit.jupiter.api.Assertions;

public class U31PurchasesStepDefs extends CucumberTestProvider {

  private long businessId;
  private long productId;
  private long inventoryItemId;
  private long listingId;

  @Given("A business owner has a business named {string}")
  public void aBusinessOwnerHasABusinessNamed(String businessName) throws Exception {
    adminLogin();
    CreateBusinessDto business = new CreateBusinessDto();
    business.setBusinessType("Retail Trade");
    business.setName(businessName);
    business.setAddress(new FullAddressDto(makeAddress()));
    business.setPrimaryAdministratorId(loggedInUserId);
    JsonNode json = makePostRequestGetJson("/businesses", business, status().isCreated());
    businessId = json.get("businessId").asLong();
  }

  @And("the business has a product {string} with price {double} and manufacturer {string}")
  public void theBusinessHasAProductWithPriceAndManufacturer(String productName, double price,
      String manufacturerName) throws Exception {
    CreateProductDto product = new CreateProductDto();
    product.setName(productName);
    product.setManufacturer(manufacturerName);
    product.setRecommendedRetailPrice(price);
    JsonNode response = makePostRequestGetJson(
        "/businesses/" + businessId + "/products",
        product,
        status().isCreated()
    );

    productId = response.get("productId").asLong();
  }

  @And("the business has {int} of them with a total price of {double}")
  public void theBusinessHasOfThemWithATotalPriceOf(int inventoryQuantity, double price)
      throws Exception {
    CreateInventoryItemDto inventoryItem = new CreateInventoryItemDto();
    inventoryItem.setQuantity(inventoryQuantity);
    inventoryItem.setTotalPrice(price);
    inventoryItem.setProductId(productId);
    inventoryItem.setExpires(LocalDate.now().plusWeeks(2));
    JsonNode response = makePostRequestGetJson(
        "/businesses/" + businessId + "/inventory",
        inventoryItem,
        status().isCreated()
    );

    inventoryItemId = response.get("inventoryItemId").asLong();
  }

  @And("there are {int} listings for {int} of the products with a price of {double} for each listing")
  public void thereAreListingsForOfTheProductsWithAPriceOfForEachListing(int listingAmount,
      int listingQuantity,
      double listingPrice) throws Exception {
    CreateListingDto listing;

    for (int i = 0; i < listingAmount; i++) {
      listing = new CreateListingDto();
      listing.setInventoryItemId(inventoryItemId);
      listing.setQuantity(listingQuantity);
      listing.setPrice(listingPrice);

      JsonNode response = makePostRequestGetJson(
          "/businesses/" + businessId + "/listings",
          listing,
          status().isCreated()
      );

      listingId = response.get("listingId").asLong();  // Only sets the last listing DTO
    }
  }

  @When("A user purchases one of the listings")
  public void aUserPurchasesOneOfTheListings() throws Exception {
    mockMvc.perform(delete(
        String.format("/businesses/%d/listings/%d/purchase", businessId, listingId)
    ));
  }

  @Then("the corresponding inventory-item's quantity decreases by the correct amount")
  public void theCorrespondingInventoryItemSQuantityDecreasesByTheCorrectAmount() throws Exception {
    InventoryItem inventoryItem = inventoryService
        .getInventoryItemById(businessId, inventoryItemId);

    Assertions.assertEquals(3, inventoryItem.getQuantity());
  }

  @Then("the inventory-item's listing no longer exists")
  public void theInventoryItemSListingNoLongerExists() {
    Assertions.assertThrows(
        ListingNotFoundException.class,
        () -> listingService.getListing(listingId)
    );
  }
}
