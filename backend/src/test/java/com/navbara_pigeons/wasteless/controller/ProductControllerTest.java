package com.navbara_pigeons.wasteless.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.testprovider.ControllerTestProvider;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;

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
        JSONObject mockProduct = new JSONObject();
        mockProduct.put("name", "Pizza");

        mockMvc.perform(post("/businesses/1/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockProduct)))
            .andExpect(status().isCreated());
    }

    // Throw 400 on bad request to controller (name is required)
    @Test
    @WithUserDetails(value="dnb36@uclive.ac.nz")
    public void throw400OnBadProductTest() throws Exception {
        JSONObject mockProduct = new JSONObject();
        mockProduct.put("name", null);

        mockMvc.perform(post("/businesses/1/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockProduct)))
            .andExpect(status().isBadRequest());
    }

    // Throw 401 when unauthorized
    @Test
    @WithAnonymousUser
    public void throw401OnAddProductTest() throws Exception {
        JSONObject mockProduct = new JSONObject();
        mockProduct.put("name", "Pizza");

        mockMvc.perform(post("/businesses/1/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockProduct)))
            .andExpect(status().isUnauthorized());
    }

    // Throw 403 when not business admin
    @Test
    @WithUserDetails(value="amf133@uclive.ac.nz")
    public void throw403OnAddProductTest() throws Exception {
        JSONObject mockProduct = new JSONObject();
        mockProduct.put("name", "Pizza");

        mockMvc.perform(post("/businesses/1/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(mockProduct)))
            .andExpect(status().isForbidden());
    }
}
