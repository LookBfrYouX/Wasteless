package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
}
