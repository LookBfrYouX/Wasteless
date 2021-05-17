package com.navbara_pigeons.wasteless.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithUserDetails;

public class ListingController extends ControllerTestProvider {

  // Return 201 on successful request to controller
  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  public void return201OnAddProductTest() throws Exception {
    Listing mockListing = new Listing();
    mockListing.setQuantity(3);
    mockListing.setPrice(17.99f);
    mockListing.setMoreInfo("Seller may be willing to consider near offers");
    mockListing.setCloses(ZonedDateTime.parse("2021-07-21T23:59:00Z"));

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
}
