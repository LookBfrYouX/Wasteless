package com.navbara_pigeons.wasteless.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.navbara_pigeons.wasteless.dto.CreateInventoryDto;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

import java.time.LocalDate;

public class InventoryControllerTest extends ControllerTestProvider {

  @Test
  @WithUserDetails(value = "mbi47@uclive.ac.nz")
  void getInventoryFromOneBusinessTestAsAdmin() throws Exception {
    String endpointUrl = "/businesses/1/inventory";
    mockMvc.perform(get(endpointUrl)).andExpect(status().isOk());
  }

  @Test
  @WithUserDetails(value = "fdi19@uclive.ac.nz")
  void getInventoryFromOneBusinessTestAsWrongUser() throws Exception {
    String endpointUrl = "/businesses/3/inventory";
    mockMvc.perform(get(endpointUrl)).andExpect(status().is(403));
  }

  @Test
  @WithUserDetails(value = "fdi19@uclive.ac.nz")
  void getInventoryFromOneNonExistingBusinessTest() throws Exception {
    String endpointUrl = "/businesses/9999/inventory";
    mockMvc.perform(get(endpointUrl)).andExpect(status().is(406));
  }

  @Test
  @WithAnonymousUser
  void getInventoryFromOneBusinessTestAsAnon() throws Exception {
    String endpointUrl = "/businesses/1/inventory";
    mockMvc.perform(get(endpointUrl)).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  void getInventoryFromOneBusinessTestInvalidId() throws Exception {
    String endpointUrl = "/businesses/-1/inventory";
    mockMvc.perform(get(endpointUrl)).andExpect(status().is(406));
  }

  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void addInventoryItemToBusinessInventory() throws Exception {
    String endpointUrl = "/businesses/1/inventory";
    CreateInventoryDto dto = new CreateInventoryDto();
    dto.setProductId(1);
    dto.setQuantity(2);
    dto.setExpires(LocalDate.now());
    mockMvc.perform(post(endpointUrl)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isCreated());
  }

  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void addInventoryItemToBusinessInventoryInvalidBusinessId() throws Exception {
    String endpointUrl = "/businesses/5/inventory";
    CreateInventoryDto dto = new CreateInventoryDto();
    dto.setProductId(1);
    dto.setQuantity(2);
    dto.setExpires(LocalDate.now());
    mockMvc.perform(post(endpointUrl)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(400));
  }

  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void addInventoryItemToBusinessInventoryInvalidProductId() throws Exception {
    String endpointUrl = "/businesses/1/inventory";
    CreateInventoryDto dto = new CreateInventoryDto();
    dto.setProductId(7000);
    dto.setQuantity(2);
    dto.setExpires(LocalDate.now());
    mockMvc.perform(post(endpointUrl)
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().is(400));
  }

}
