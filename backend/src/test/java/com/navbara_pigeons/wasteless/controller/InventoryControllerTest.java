package com.navbara_pigeons.wasteless.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.navbara_pigeons.wasteless.dto.CreateInventoryItemDto;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

public class InventoryControllerTest extends ControllerTestProvider {

  @Test
  @WithUserDetails(value = "mbi47@uclive.ac.nz")
  void getInventoryFromOneBusinessTestAsAdmin() throws Exception {
    String endpointUrl = "/businesses/1001/inventory";
    mockMvc.perform(get(endpointUrl)).andExpect(status().isOk());
  }

  @Test
  @WithUserDetails(value = "fdi19@uclive.ac.nz")
  void getInventoryFromOneBusinessTestAsWrongUser() throws Exception {
    String endpointUrl = "/businesses/1003/inventory";
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
    String endpointUrl = "/businesses/1001/inventory";
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
    String endpointUrl = "/businesses/1001/inventory";
    CreateInventoryItemDto dto = new CreateInventoryItemDto();
    dto.setProductId(5001);
    dto.setQuantity(2);
    dto.setExpires(LocalDate.now());
    mockMvc.perform(post(endpointUrl)
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isCreated());
  }

  @Test
  @WithUserDetails(value = "mbi47@uclive.ac.nz")
  void asUser_addInventoryItem_expectOk() throws Exception {
    String endpointUrl = "/businesses/1002/inventory";
    CreateInventoryItemDto inventoryItemDto = new CreateInventoryItemDto();
    inventoryItemDto.setExpires(LocalDate.now());
    inventoryItemDto.setProductId(5002);
    inventoryItemDto.setQuantity(15);
    inventoryItemDto.setTotalPrice(220.00);
    inventoryItemDto.setPricePerItem(20.00);
    mockMvc.perform(post(endpointUrl)
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(inventoryItemDto)))
        .andExpect(status().isCreated());
  }

  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void asUser_addInventoryItem_expect_InvalidBusinessId() throws Exception {
    String endpointUrl = "/businesses/-1/inventory";
    CreateInventoryItemDto dto = new CreateInventoryItemDto();
    dto.setProductId(5001);
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
    String endpointUrl = "/businesses/1001/inventory";
    CreateInventoryItemDto dto = new CreateInventoryItemDto();
    dto.setProductId(7000);
    dto.setQuantity(2);
    dto.setExpires(LocalDate.now());
    mockMvc.perform(post(endpointUrl)
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().is(400));
  }

  @Test
  @WithMockUser
  void asUser_addInventoryItemToNonExistingBusiness_expectNotFound() throws Exception {
    String endpointUrl = "/businesses/200000/inventory";
    CreateInventoryItemDto inventoryItemDto = new CreateInventoryItemDto();
    inventoryItemDto.setExpires(LocalDate.now());
    inventoryItemDto.setProductId(5002);
    inventoryItemDto.setQuantity(15);
    inventoryItemDto.setTotalPrice(220.00);
    inventoryItemDto.setPricePerItem(20.00);
    mockMvc.perform(post(endpointUrl)
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(inventoryItemDto)))
        .andExpect(status().is(400));
  }

  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void asSpecificUser_addInventoryItemToOtherUsersBusiness_expectForbidden() throws Exception {
    String endpointUrl = "/businesses/1003/inventory";
    CreateInventoryItemDto inventoryItemDto = new CreateInventoryItemDto();
    inventoryItemDto.setExpires(LocalDate.now());
    inventoryItemDto.setProductId(5002);
    inventoryItemDto.setQuantity(15);
    inventoryItemDto.setTotalPrice(220.00);
    inventoryItemDto.setPricePerItem(20.00);
    mockMvc.perform(post(endpointUrl)
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(inventoryItemDto)))
        .andExpect(status().isForbidden());
  }

  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void addInventoryItemToBusinessInventoryInvalidPrice() throws Exception {
    String endpointUrl = "/businesses/1001/inventory";
    CreateInventoryItemDto dto = new CreateInventoryItemDto();
    dto.setProductId(7000);
    dto.setQuantity(2);
    dto.setPricePerItem(-5.00);
    dto.setExpires(LocalDate.now());
    mockMvc.perform(post(endpointUrl)
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());

    dto.setPricePerItem(10000001.00);

    mockMvc.perform(post(endpointUrl)
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());

    dto.setPricePerItem(5.00);
    dto.setTotalPrice(-5.00);

    mockMvc.perform(post(endpointUrl)
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());

    dto.setTotalPrice(10000001.00);

    mockMvc.perform(post(endpointUrl)
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(dto)))
        .andExpect(status().isBadRequest());
  }
}
