package com.navbara_pigeons.wasteless.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.navbara_pigeons.wasteless.dto.CreateListingDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.AddressValidationException;
import com.navbara_pigeons.wasteless.exception.BusinessRegistrationException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MvcResult;

class ListingControllerTest extends ControllerTestProvider {

  @Test
  @WithAnonymousUser
  void searchListings_anonUser_expectUnauthorized() throws Exception {
    mockMvc.perform(get("/listings/search")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(value = "mbi47@uclive.ac.nz")
  void searchListings_validUser_expectOk() throws Exception {
    mockMvc.perform(get("/listings/search")
        .param("pagStartIndex", "1")
        .param("pagEndIndex", "2")
        .param("sortBy", "QUANTITY")
        .param("isAscending", "true")
        .param("searchKeys", "PRODUCT_NAME")
    ).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(value = "mbi47@uclive.ac.nz")
  void searchListings_withValidParams_expectOk() throws Exception {
    mockMvc.perform(
            get("/listings/search")
                    .param("pagStartIndex", "1")
                    .param("pagEndIndex", "2")
                    .param("sortBy", "QUANTITY")
                    .param("searchKeys", "ADDRESS")
                    .param("isAscending", "true")
                    .param("searchParam", "New Zealand")
                    .param("fat", "HIGH")
                    .param("saturatedFat","HIGH")
                    .param("saturatedFat", "MODERATE")
                    .param("sugars", "LOW")
                    .param("salt", "LOW")
    ).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(value = "mbi47@uclive.ac.nz")
  void searchListings_withInvalidFatValue_expectBadRequest() throws Exception {
    mockMvc.perform(
            get("/listings/search")
                    .param("fat", "BOOGALOO")
    ).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(value = "mbi47@uclive.ac.nz")
  void searchListings_withInvalidSaturatedFatValue_expectBadRequest() throws Exception {
    mockMvc.perform(
            get("/listings/search")
                    .param("saturatedFat", "BOOGALOO")
    ).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(value = "mbi47@uclive.ac.nz")
  void searchListings_withInvalidSugarsValue_expectBadRequest() throws Exception {
    mockMvc.perform(
            get("/listings/search")
                    .param("sugars", "BOOGALOO")
    ).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(value = "mbi47@uclive.ac.nz")
  void searchListings_withInvalidSaltValue_expectBadRequest() throws Exception {
    mockMvc.perform(
            get("/listings/search")
                    .param("salt", "BOOGALOO")
    ).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(value = "mbi47@uclive.ac.nz")
  void searchListings_withValidDietaryFilterParams_expectOk() throws Exception {
    mockMvc.perform(
            get("/listings/search")
                    .param("pagStartIndex", "1")
                    .param("pagEndIndex", "2")
                    .param("sortBy", "QUANTITY")
                    .param("searchKeys", "ADDRESS")
                    .param("isAscending", "true")
                    .param("searchParam", "New Zealand")
                    .param("isVegan", "true")
                    .param("isVegetarian", "true")
                    .param("isGlutenFree", "true")
                    .param("isPalmOilFree", "true")
    ).andExpect(status().isOk());
  }

  @Test
  @WithMockUser(value = "mbi47@uclive.ac.nz")
  void searchListings_withValidDietaryFilterParams_expectBadRequest() throws Exception {
    mockMvc.perform(
            get("/listings/search")
                    .param("pagStartIndex", "1")
                    .param("pagEndIndex", "2")
                    .param("sortBy", "QUANTITY")
                    .param("searchKeys", "ADDRESS")
                    .param("isAscending", "true")
                    .param("searchParam", "New Zealand")
                    .param("isVegan", "Blah")
    ).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(value = "mbi47@uclive.ac.nz")
  void searchListings_withInvalidSearchKeys_expectBadRequest() throws Exception {
    mockMvc.perform(
            get("/listings/search")
                    .param("searchKeys", "Big Mac?")
    ).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(value = "mbi47@uclive.ac.nz")
  void searchListings_withInvalidPaginationParams_expectBadRequest() throws Exception {
    mockMvc.perform(
            get("/listings/search")
                    .param("pagStartIndex", "-1")
                    .param("pagEndIndex", "-1")
    ).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser(value = "mbi47@uclive.ac.nz")
  void searchListings_withInvalidParams_expectException() throws Exception {
    mockMvc.perform(
            get("/listings/search")
                    .param("pagStartIndex", "-1")
    ).andExpect(status().isBadRequest());
  }

  @Test
  @WithMockUser
  void getListings_normalUser() throws Exception {
    mockMvc.perform(get("/businesses/1001/listings")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void getListings_badId() throws Exception {
    mockMvc.perform(get("/businesses/10000/listings")).andExpect(status().isNotFound());
  }

  // Return 201 on successful request to controller
  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void return201OnAddListing() throws Exception {
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(5007);
    listing.setQuantity(1L);
    listing.setPrice(17.99);

    mockMvc.perform(post("/businesses/1001/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andExpect(status().isCreated());

  }

  // Throw 400 on bad request to controller (quantity is required)
  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void throw400OnBadListing() throws Exception {
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(5001);
    listing.setPrice(17.99);

    mockMvc.perform(post("/businesses/1001/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andExpect(status().isBadRequest());
  }

  // Throw 400 on bad request to controller (price must be above 0 and below 10000000)
  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void throw400OnBadListingPrice() throws Exception {
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(5001);
    listing.setQuantity(1);
    listing.setPrice(-5.00);

    mockMvc.perform(post("/businesses/1001/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andExpect(status().isBadRequest());

    listing.setPrice(10000001.00);

    mockMvc.perform(post("/businesses/1001/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andExpect(status().isBadRequest());
  }

  // Throw 401 when unauthorized
  @Test
  @WithAnonymousUser
  void throw401OnAddListingTest() throws Exception {
    Listing mockListing = new Listing();
    mockListing.setQuantity(1L);
    mockListing.setPrice(17.99);

    mockMvc.perform(post("/businesses/1001/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(mockListing)))
        .andExpect(status().isUnauthorized());
  }

  // Throw 403 when not business admin or admin
  @Test
  @WithUserDetails(value = "fdi19@uclive.ac.nz")
  public void throw403OnAddListingTest() throws Exception {
    Listing mockListing = new Listing();
    mockListing.setQuantity(1L);
    mockListing.setPrice(17.99);

    mockMvc.perform(post("/businesses/1001/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(mockListing)))
        .andExpect(status().isForbidden());
  }


  private CreateListingDto makeListing()
      throws UserNotFoundException, BusinessTypeException, AddressValidationException, BusinessRegistrationException {
    Business business = makeBusiness();
    Product product = makeProduct("Stoopid");
    InventoryItem inventoryItem = makeInventoryItem(product, business);
    business.addInventoryItem(inventoryItem);

    businessService.saveBusiness(business);

    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(1L);
    listing.setQuantity(1L);
    listing.setPrice(17.99);

    return listing;
  }
}
