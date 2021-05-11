package com.navbara_pigeons.wasteless.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.service.InventoryService;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

public class InventoryControllerTest extends ControllerTestProvider {

  @Mock
  InventoryService inventoryServiceMock;

  @Test
  @WithUserDetails(value = "mbi47@uclive.ac.nz")
  void getInventoryFromOneBusinessTestAsAdmin() throws Exception {
    String endpointUrl = "/businesses/1/inventory";
    when(inventoryServiceMock.getInventory(1)).thenReturn(new ArrayList<>());
    mockMvc.perform(get(endpointUrl)).andExpect(status().isOk());
  }

  @Test
  @WithUserDetails(value = "fdi19@uclive.ac.nz")
  void getInventoryFromOneBusinessTestAsWrongUser() throws Exception {
    String endpointUrl = "/businesses/3/inventory";
    when(inventoryServiceMock.getInventory(3)).thenThrow(InsufficientPrivilegesException.class);
    mockMvc.perform(get(endpointUrl)).andExpect(status().is(403));
  }

  @Test
  @WithUserDetails(value = "fdi19@uclive.ac.nz")
  void getInventoryFromOneNonExistingBusinessTest() throws Exception {
    String endpointUrl = "/businesses/9999/inventory";
    when(inventoryServiceMock.getInventory(9999)).thenThrow(BusinessNotFoundException.class);
    mockMvc.perform(get(endpointUrl)).andExpect(status().is(406));
  }

  @Test
  @WithAnonymousUser
  void getInventoryFromOneBusinessTestAsAnon() throws Exception {
    String endpointUrl = "/businesses/1/inventory";
    //not sure how to mock Unauthorized
    mockMvc.perform(get(endpointUrl)).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  void getInventoryFromOneBusinessTestInvalidId() throws Exception {
    String enpointUrl = "/businesses/-1/inventory";
    when(inventoryServiceMock.getInventory(-1)).thenThrow(BusinessNotFoundException.class);
    mockMvc.perform(get(enpointUrl)).andExpect(status().is(406));
  }
}
