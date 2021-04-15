package com.navbara_pigeons.wasteless.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.entity.Product;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
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

    // Throws a 400 status code on a bad request to the controller
    @Test
    public void throwErrorOnBadProductTest() throws Exception {
        Product product = new Product(null, null, "Hello", 40.99);

        mockMvc.perform(MockMvcRequestBuilders
            .post("/businesses/{id}/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isBadRequest());
    }

    // Throws a 201 status code on a successful request to the controller
    @Test
    public void return201OnAddProductTest() throws Exception {
        Product product = new Product("WATT-420-BEANS", "Watties Baked Beans - 420g can", "Baked Beans as they should be.", 2.2);

        mockMvc.perform(MockMvcRequestBuilders
            .post("/businesses/{id}/products")
            .contentType("application/json")
            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isCreated());
    }
}
