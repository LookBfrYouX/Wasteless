package com.navbara_pigeons.wasteless.cucumber;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.navbara_pigeons.wasteless.dto.BasicProductCreationDto;
import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.dto.FullAddressDto;
import com.navbara_pigeons.wasteless.entity.*;
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
    private JsonNode response;
    private CreateInventoryItemDto inventoryItem;

    // background is run before each test
    @Given("A user is logged in")
    public void aUserIsLoggedIn() {
        login();
    }

    @And("has a business {string} with type {string}")
    public void hasABusiness(String businessName, String businessType) throws Exception {
        Business business = new Business();
        business.setName(businessName);
        Address address = makeAddress();
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
        product.setManufacturer("Should be optional but might still be required");
        product.setRecommendedRetailPrice(10.0);
        JsonNode response = makePostRequestGetJson(
                "/businesses/" + businessId + "/products",
                product,
                status().isCreated()
        );

        productId = response.get("productId").asLong();
    }

    // scenario 2
    @When("I add an inventory entry, with the a product with quantity {int} and expiry date in the future")
    public void iAddAnInventoryEntryWithTheAProductWithQuantityAndExpiryDateInTheFuture(int quantity) throws Exception {
        CreateInventoryItemDto inventoryItem = new CreateInventoryItemDto();
        inventoryItem.setQuantity(quantity);
        inventoryItem.setProductId(productId);
        inventoryItem.setExpires(LocalDate.now());
        JsonNode response = makePostRequestGetJson(
                "/businesses/" + businessId + "/inventory",
                inventoryItem,
                status().isCreated()
        );
    }

    @Then("When I retrieve my inventory the entry is listed")
    public void whenIRetrieveMyInventoryTheEntryIsListed() throws Exception {
        response = makeGetRequestGetJson(
                "/businesses/" + businessId + "/inventory",
                status().isOk()
        );
        response.has(String.valueOf(inventoryItem));
    }



}
