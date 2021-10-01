package com.navbara_pigeons.wasteless.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.navbara_pigeons.wasteless.dto.CreateProductDto;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

class ProductControllerTest extends ControllerTestProvider {

  @Test
  @WithUserDetails(value = "mbi47@uclive.ac.nz")
  void getProductsFromOneBusinessTestAsAdmin() throws Exception {
    String endpointUrl = "/businesses/1001/products";
    mockMvc.perform(get(endpointUrl)).andExpect(status().isOk());
  }

  @Test
  @WithUserDetails(value = "fdi19@uclive.ac.nz")
  void getProductsFromOneBusinessTestAsWrongUser() throws Exception {
    String endpointUrl = "/businesses/1003/products";
    mockMvc.perform(get(endpointUrl)).andExpect(status().is(403));
  }

  @Test
  @WithUserDetails(value = "fdi19@uclive.ac.nz")
  void getProductsFromOneNonExistingBusinessTest() throws Exception {
    String endpointUrl = "/businesses/9999/products";
    mockMvc.perform(get(endpointUrl)).andExpect(status().isNotFound());
  }

  @Test
  @WithAnonymousUser
  void getProductsFromOneBusinessTestAsAnon() throws Exception {
    String endpointUrl = "/businesses/1001/products";
    mockMvc.perform(get(endpointUrl)).andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser
  void getProductsFromOneBusinessTestInvalidId() throws Exception {
    String endpointUrl = "/businesses/-1/products";
    mockMvc.perform(get(endpointUrl)).andExpect(status().isNotFound());
  }

  // Return 201 on successful request to controller
  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void return201OnAddProductTest() throws Exception {
    CreateProductDto mockProduct = new CreateProductDto();
    mockProduct.setName("Pizza");
    mockProduct.setManufacturer("Hut");
    mockProduct.setRecommendedRetailPrice(100.0);

    mockMvc.perform(post("/businesses/1001/products")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(mockProduct)))
        .andExpect(status().isCreated());
  }

  // Throw 400 on bad request to controller (name is required)
  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void throw400OnBadProductTest() throws Exception {
    CreateProductDto mockProduct = new CreateProductDto();
    mockProduct.setManufacturer(null);
    mockProduct.setRecommendedRetailPrice(100.0);

    mockMvc.perform(post("/businesses/1001/products")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(mockProduct)))
        .andExpect(status().isBadRequest());
  }

  // Throw 400 on bad request to controller (price must be above 0 and below 10000000)
  @Test
  @WithUserDetails(value = "dnb36@uclive.ac.nz")
  void throw400OnBadProductPriceTest() throws Exception {
    CreateProductDto mockProduct = new CreateProductDto();
    mockProduct.setName("Pizza");
    mockProduct.setManufacturer(null);
    mockProduct.setRecommendedRetailPrice(-5.00);

    mockMvc.perform(post("/businesses/1001/products")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(mockProduct)))
        .andExpect(status().isBadRequest());

    mockProduct.setRecommendedRetailPrice(10000001.00);

    mockMvc.perform(post("/businesses/1001/products")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(mockProduct)))
        .andExpect(status().isBadRequest());
  }

  // Throw 401 when unauthorized
  @Test
  @WithAnonymousUser
  void throw401OnAddProductTest() throws Exception {
    CreateProductDto mockProduct = new CreateProductDto();
    mockProduct.setName("Pizza");
    mockProduct.setManufacturer("Hut");
    mockProduct.setRecommendedRetailPrice(100.0);

    mockMvc.perform(post("/businesses/1001/products")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(mockProduct)))
        .andExpect(status().isUnauthorized());
  }

  // Throw 403 when not business admin or admin
  @Test
  @WithUserDetails(value = "fdi19@uclive.ac.nz")
  void throw403OnAddProductTest() throws Exception {
    CreateProductDto mockProduct = new CreateProductDto();
    mockProduct.setName("Pizza");
    mockProduct.setManufacturer("Hut");
    mockProduct.setRecommendedRetailPrice(100.0);

    mockMvc.perform(post("/businesses/1001/products")
        .contentType("application/json")
        .content(objectMapper.writeValueAsString(mockProduct)))
        .andExpect(status().isForbidden());
  }
}
