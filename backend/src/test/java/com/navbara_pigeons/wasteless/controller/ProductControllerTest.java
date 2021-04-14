package com.navbara_pigeons.wasteless.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductController.class)
public class ProductControllerTest {

    @Autowired
    public MockMvc mockMvc;

    // TODO: create product entity
    @MockBean
    private Product product;

    @Autowired
    private ObjectMapper objectMapper;

    // Throws a 400 status code on a bad request to the controller
    @Test
    public void throwErrorOnBadProductTest() throws Exception {
        // TODO: This product might need to be created before these tests will pass
        Product product = new Product(null, null, 3, "Tony");

        mockMvc.perform(post("/businesses/{id}/products")
            .contentType("application/json")
            .param("businessId", "true")
            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isBadRequest());
    }

    // Throws a 201 status code on a successful request to the controller
    @Test
    public void return201OnAddProductTest() throws Exception {
        // TODO: This product might need to be created before these tests will pass
        Product product = new Product("WATT-420-BEANS", "Watties Baked Beans - 420g can", "Baked Beans as they should be.", 2.2);

        mockMvc.perform(post("/businesses/{id}/products")
            .contentType("application/json")
            .param("businessId", "true")
            .content(objectMapper.writeValueAsString(product)))
            .andExpect(status().isCreated());
    }
}
