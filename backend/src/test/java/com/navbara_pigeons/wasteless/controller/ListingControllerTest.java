package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.CreateListingDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.service.BusinessService;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ListingControllerTest extends ControllerTestProvider {
  @Test
//  @WithUserDetails(value = "mbi47@uclive.ac.nz")
  @WithMockUser
  void getListings_normalUser() throws Exception {
    mockMvc.perform(get("/businesses/1/listings")).andExpect(status().isOk());
  }

  @Test
  @WithMockUser
  void getListings_badId() throws Exception {
    mockMvc.perform(get("/businesses/10000/listings")).andExpect(status().isNotAcceptable());
  }

  // Return 201 on successful request to controller
  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  public void return201OnAddProductTest() throws Exception {
    CreateListingDto mockListing = makeListing();

    mockMvc.perform(post("/businesses/1/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(mockListing)))
        .andExpect(status().isCreated());
  }

  // Throw 400 on bad request to controller (name is required)
  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  public void throw400OnBadListingTest() throws Exception {
    Listing mockListing = new Listing();
    mockListing.setPrice(17.99f);

    mockMvc.perform(post("/businesses/1/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(mockListing)))
        .andExpect(status().isBadRequest());
  }

  // Throw 401 when unauthorized
  @Test
  @WithAnonymousUser
  public void throw401OnAddProductTest() throws Exception {
    Listing mockListing = new Listing();
    mockListing.setQuantity(1);
    mockListing.setPrice(17.99f);

    mockMvc.perform(post("/businesses/1/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(mockListing)))
        .andExpect(status().isUnauthorized());
  }

  // Throw 403 when not business admin or admin
  @Test
  @WithUserDetails(value = "fdi19@uclive.ac.nz")
  public void throw403OnAddProductTest() throws Exception {
    Listing mockListing = new Listing();
    mockListing.setQuantity(1);
    mockListing.setPrice(17.99f);

    mockMvc.perform(post("/businesses/1/listings")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(mockListing)))
        .andExpect(status().isForbidden());
  }

  private CreateListingDto makeListing() {
    Business business = makeBusiness();
    Product product = makeProduct("Stoopid");
    InventoryItem inventoryItem = makeInventoryItem(product, business);
    business.addInventoryItem(inventoryItem);

    try {
      businessService.saveBusiness(business);
    } catch (Exception exc) {
      exc.printStackTrace();
    }

    CreateListingDto listing = new CreateListingDto();
    listing.setInventoryItemId(1);
    listing.setQuantity(4);
    listing.setPrice(17.99f);
    listing.setInventoryItemId(1);

    return listing;
  }
}
