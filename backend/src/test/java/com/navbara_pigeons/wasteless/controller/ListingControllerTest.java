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

public class ListingControllerTest extends ControllerTestProvider {

  @Test
  @WithMockUser
  void getListings_normalUser() throws Exception {
    mockMvc.perform(get("/businesses/1001/listings")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void getListings_badId() throws Exception {
    mockMvc.perform(get("/businesses/10000/listings")).andExpect(status().isNotAcceptable());
  }

  // Return 201 on successful request to controller
  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  public void return201OnAddListing() throws Exception {
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(5001);
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
  public void throw400OnBadListing() throws Exception {
    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(5001);
    listing.setPrice(17.99);

    mockMvc.perform(post("/businesses/1001/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(listing)))
        .andExpect(status().isBadRequest());
  }

  // Throw 401 when unauthorized
  @Test
  @WithAnonymousUser
  public void throw401OnAddListingTest() throws Exception {
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
