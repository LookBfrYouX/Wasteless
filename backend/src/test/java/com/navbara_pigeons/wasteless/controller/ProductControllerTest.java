package com.navbara_pigeons.wasteless.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.entity.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(username = "admin@wasteless.co.nz", password = "admin")
    void getProductsFromOneBusinessTestAsAdmin() throws Exception {
        String endpointUrl = "/businesses/1/products";
        this.mockMvc.perform(get(endpointUrl)).andExpect(status().isOk());
    }

    @Test
    void getProductsFromOneBusinessTestAsAnon() throws Exception {
        String endpointUrl = "/businesses/1/products";
        this.mockMvc.perform(get(endpointUrl)).andExpect(status().isUnauthorized());
    }

    // Throw 201 on successful request to controller
    @Test
    @WithMockUser(username = "dnb36@uclive.ac.nz", password = "fun123")
    public void return201OnAddProductTest() throws Exception {
        Product product = new Product("WATT-420-BEANS", "Watties Baked Beans - 420g can", "Baked Beans as they should be.", 2.2);

        mockMvc.perform(post("/businesses/1/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isCreated());
    }

    // Throws 400 on bad request to controller
    @Test
    @WithMockUser(username = "dnb36@uclive.ac.nz", password = "fun123")
    public void throw400OnBadProductTest() throws Exception {
        Product product = new Product(null, null, "Hello", 40.99);

        mockMvc.perform(post("/businesses/1/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isBadRequest());
    }

    // Throw 401 on bad request to controller
    @Test
    @WithAnonymousUser
    public void return401OnAddProductTest() throws Exception {
        Product product = new Product("WATT-420-BEANS", "Watties Baked Beans - 420g can", "Baked Beans as they should be.", 2.2);

        mockMvc.perform(post("/businesses/1/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isUnauthorized());
    }

    // Throw 403 on bad request to controller
    @Test
    @WithMockUser(username = "tony@gmail.com", password = "tony")
    public void return403OnAddProductTest() throws Exception {
        Product product = new Product("WATT-420-BEANS", "Watties Baked Beans - 420g can", "Baked Beans as they should be.", 2.2);

        mockMvc.perform(post("/businesses/1/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isForbidden());
    }
}
