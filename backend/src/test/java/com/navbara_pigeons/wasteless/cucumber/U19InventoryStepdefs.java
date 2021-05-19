package com.navbara_pigeons.wasteless.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.dto.BasicProductCreationDto;
import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.FullAddressDto;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import lombok.SneakyThrows;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDate;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class U19InventoryStepdefs extends CucumberTestProvider {

    private long businessId;
    private long productId;
    private MvcResult response;

    // background is run before each test
    @Given("A user is logged in")
    public void aUserIsLoggedIn() {
        login();
    }

    @And("has a business {string} with type {string}")
    public void hasABusiness(String businessName, String businessType) throws Exception {
        CreateBusinessDto business = new CreateBusinessDto();
        business.setName(businessName);
        FullAddressDto address = new FullAddressDto();
        business.setBusinessType(businessType)
                .setName(businessName)
                .setAddress(address);

        JsonNode response = makePostRequestGetJson(
                "/businesses/" ,
                business,
                status().isCreated());
       businessId = response.get("businessId").asLong();
    }

    @And("with a product {string}")
    public void withAProduct(String name) throws Exception {
        BasicProductCreationDto product = new BasicProductCreationDto();
        product.setName(name);
        product.setRecommendedRetailPrice(10.0);
        JsonNode response = makePostRequestGetJson(
                "/businesses/" + businessId + "/products",
                product,
                status().isCreated()
        );
        long productId = response.get("productId").asLong();
    }


    // Scenario 1
    @Given("the user has created an inventory item with the product")
    public void theUserHasCreatedAnInventoryItemWithTheProduct() throws Exception {
        CreateInventoryItemDto inventoryItem = new CreateInventoryItemDto();
        inventoryItem.setQuantity(10);
        inventoryItem.setProductId(this.productId);
        inventoryItem.setExpires(LocalDate.now());
        JsonNode response = makePostRequestGetJson(
                "/businesses/" + businessId + "/inventory",
                inventoryItem,
                status().isCreated()
        );
    }

    @When("I retrieve my inventory")
    public void iRetrieveMyInventory() {


    }

    @Then("The inventory item is listed")
    public void theInventoryItemIsListed() {
    }

    @When("Someone else retrieves my inventory")
    public void someoneElseRetrievesMyInventory() {
    }

    @Then("An error is shown")
    public void anErrorIsShown() {
    }


    // scenario 2
    @When("I add an inventory entry, with the a product with quantity {int} and expiry date in the future")
    public void iAddAnInventoryEntryWithTheAProductWithQuantityAndExpiryDateInTheFuture(int quantity) throws Exception {
        CreateInventoryItemDto inventoryItem = new CreateInventoryItemDto();
        inventoryItem.setQuantity(quantity);
        inventoryItem.setProductId(this.productId);
        inventoryItem.setExpires(LocalDate.now());
        JsonNode response = makePostRequestGetJson(
                "/businesses/" + businessId + "/inventory",
                inventoryItem,
                status().isCreated()
        );
    }

    @Then("When I retrieve my inventory the entry is listed")
    public void whenIRetrieveMyInventoryTheEntryIsListed() {

    }



}
