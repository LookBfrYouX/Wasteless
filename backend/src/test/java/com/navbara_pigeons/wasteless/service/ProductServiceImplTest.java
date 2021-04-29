package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.security.model.BasicUserDetails;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import javax.transaction.Transactional;
import net.minidev.json.JSONObject;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.event.annotation.BeforeTestClass;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ProductServiceImplTest extends ServiceTestProvider {
    @Autowired
    ProductService productService;
    @Autowired
    BusinessDao businessDao;
    @Autowired
    AddressDao addressDao;

    @Test
    void getProductsTest() {
    }

    Product findProductWithName(long businessId, String productName) throws BusinessNotFoundException {
        Product savedProduct = null;
        for(Product product: productService.getProducts(String.valueOf(businessId))) {
            if (product.getName() == productName) {
                // Find product with the same name
                savedProduct = product;
                break;
            }
        }

        assertNotNull(savedProduct, "Couldn't find saved product");
        return savedProduct;
    }

    @Test
    @Disabled
    @WithUserDetails(value="dnb36@uclive.ac.nz")
    void createProductExpectOk() throws BusinessNotFoundException {
        JSONObject mockProduct = new JSONObject();
        final String name = "Pizza";
        final int businessId = 1;
        final double price = 9.99;
        final String description = "DESCRIPTION";

        mockProduct.put("name", name);
        assertDoesNotThrow(() -> productService.addProduct(businessId, mockProduct));

        Product savedProduct = this.findProductWithName(businessId, name);

        assertEquals(name, savedProduct.getName());
        assertEquals("NZD", savedProduct.getCurrency());
        assertEquals(price, savedProduct.getRecommendedRetailPrice());
        assertEquals(description, savedProduct.getDescription());
        // Delta between date created and current time less than a second
        assertTrue(Math.abs(savedProduct.getCreated().toInstant().toEpochMilli() - System.currentTimeMillis()) < 1000);
    }

    @Test
    @WithUserDetails(value="dnb36@uclive.ac.nz")
    void createProductExpectException() {
        JSONObject mockProduct = new JSONObject();
        mockProduct.put("name", null);

        assertThrows(Exception.class, () -> productService.addProduct(1, mockProduct));
    }
}

