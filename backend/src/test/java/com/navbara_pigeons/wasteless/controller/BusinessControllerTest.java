package com.navbara_pigeons.wasteless.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

public class BusinessControllerTest extends ControllerTestProvider {

  @Test
  @WithUserDetails(value = "amf133@uclive.ac.nz")
  void registerBusiness_expectCreated() throws Exception {
    CreateBusinessDto createBusinessDto = makeCreateBusinessDto();

    mockMvc.perform(post("/businesses/")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(createBusinessDto)))
        .andExpect(status().isCreated());
  }

  @Test
  @WithUserDetails(value = "amf133@uclive.ac.nz")
  void registerBusiness_expectBadRequest() throws Exception {
    CreateBusinessDto createBusinessDto = makeCreateBusinessDto();
    createBusinessDto.setBusinessType("Stupid business type that doesnt exist");

    mockMvc.perform(post("/businesses/")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(createBusinessDto)))
        .andExpect(status().isBadRequest());
  }

  @Test
  @WithAnonymousUser
  void registerBusiness_expectUnauthorized() throws Exception {
    CreateBusinessDto createBusinessDto = makeCreateBusinessDto();

    mockMvc.perform(post("/businesses/")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(createBusinessDto)))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithUserDetails(value = "amf133@uclive.ac.nz")
  void getBusiness_expectOk() throws Exception {
    mockMvc.perform(get("/businesses/1")).andExpect(status().isOk());
  }

  @Test
  @WithAnonymousUser
  void getBusiness_expectUnauthorized() throws Exception {
    mockMvc.perform(get("/businesses/1")).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  void getBusiness_expectNotAcceptable() throws Exception {
    mockMvc.perform(get("/businesses/tony")).andExpect(status().isNotAcceptable());
  }
}
