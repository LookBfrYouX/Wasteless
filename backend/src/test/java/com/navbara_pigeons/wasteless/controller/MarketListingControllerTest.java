package com.navbara_pigeons.wasteless.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

public class MarketListingControllerTest extends ControllerTestProvider {

  @Test
  @WithMockUser
  void getMarketListings_validSection() throws Exception {
    String validSection = "Wanted";
    mockMvc.perform(get("/cards?section=" + validSection)).andExpect(status().isOk());
  }

}
