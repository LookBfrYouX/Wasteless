package com.navbara_pigeons.wasteless.controller;


import com.navbara_pigeons.wasteless.dto.BasicProductCreationDto;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProductControllerTest extends ControllerTestProvider {

    @Test
    @WithUserDetails(value = "mbi47@uclive.ac.nz")
    void getProductsFromOneBusinessTestAsAdmin() throws Exception {
        String endpointUrl = "/businesses/1/products";
        mockMvc.perform(get(endpointUrl)).andExpect(status().isOk());
    }

    @Test
    @WithUserDetails(value = "fdi19@uclive.ac.nz")
    void getProductsFromOneBusinessTestAsWrongUser() throws Exception {
        String endpointUrl = "/businesses/3/products";
        mockMvc.perform(get(endpointUrl)).andExpect(status().is(403));
    }

    @Test
    @WithUserDetails(value = "fdi19@uclive.ac.nz")
    void getProductsFromOneNonExistingBusinessTest() throws Exception {
        String endpointUrl = "/businesses/9999/products";
        mockMvc.perform(get(endpointUrl)).andExpect(status().is(406));
    }

    @Test
    @WithAnonymousUser
    void getProductsFromOneBusinessTestAsAnon() throws Exception {
        String endpointUrl = "/businesses/1/products";
        mockMvc.perform(get(endpointUrl)).andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getProductsFromOneBusinessTestInvalidId() throws Exception {
        String enpointUrl = "/businesses/-1/products";
        mockMvc.perform(get(enpointUrl)).andExpect(status().is(406));
    }

    // Return 201 on successful request to controller
    @Test
    @WithUserDetails(value="dnb36@uclive.ac.nz")
    public void return201OnAddProductTest() throws Exception {
        BasicProductCreationDto mockProduct = new BasicProductCreationDto();
        mockProduct.setName("Pizza");
        mockProduct.setManufacturer("Hut");
        mockProduct.setRecommendedRetailPrice(100.0);

        mockMvc.perform(post("/businesses/1/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockProduct)))
            .andExpect(status().isCreated());
    }

    // Throw 400 on bad request to controller (name is required)
    @Test
    @WithUserDetails(value="dnb36@uclive.ac.nz")
    public void throw400OnBadProductTest() throws Exception {
        BasicProductCreationDto mockProduct = new BasicProductCreationDto();
        mockProduct.setName("Pizza");
        mockProduct.setManufacturer(null);
        mockProduct.setRecommendedRetailPrice(100.0);

        mockMvc.perform(post("/businesses/1/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockProduct)))
            .andExpect(status().isBadRequest());
    }

    // Throw 401 when unauthorized
    @Disabled
    @Test
    @WithAnonymousUser
    public void throw401OnAddProductTest() throws Exception {
        BasicProductCreationDto mockProduct = new BasicProductCreationDto();
        mockProduct.setName("Pizza");
        mockProduct.setManufacturer("Hut");
        mockProduct.setRecommendedRetailPrice(100.0);

        mockMvc.perform(post("/businesses/1/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockProduct)))
            .andExpect(status().isUnauthorized());
    }

    // Throw 403 when not business admin or admin
    @Test
    @WithUserDetails(value="fdi19@uclive.ac.nz")
    public void throw403OnAddProductTest() throws Exception {
        BasicProductCreationDto mockProduct = new BasicProductCreationDto();
        mockProduct.setName("Pizza");
        mockProduct.setManufacturer("Hut");
        mockProduct.setRecommendedRetailPrice(100.0);

        mockMvc.perform(post("/businesses/1/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockProduct)))
            .andExpect(status().isForbidden());
    }
}
