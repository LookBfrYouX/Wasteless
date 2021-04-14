package com.navbara_pigeons.wasteless.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.entity.Product;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductController.class)
public class ProductControllerTest {

    @Autowired
    public MockMvc mockMvc;

    @MockBean
    private Product product;

    @Autowired
    private ObjectMapper objectMapper;

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
