package com.navbara_pigeons.wasteless.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.navbara_pigeons.wasteless.dao.AddressDao;
import com.navbara_pigeons.wasteless.dao.BusinessDao;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import javax.transaction.Transactional;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

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

    @Test
    @WithUserDetails(value="dnb36@uclive.ac.nz")
    void createProductExpectOk() {
        JSONObject mockProduct = new JSONObject();
        mockProduct.put("name", "Pizza");

        assertDoesNotThrow(() -> productService.addProduct(1, mockProduct));
    }

    @Test
    @WithUserDetails(value="dnb36@uclive.ac.nz")
    void createProductExpectException() {
        JSONObject mockProduct = new JSONObject();
        mockProduct.put("name", null);

        assertThrows(Exception.class, () -> productService.addProduct(1, mockProduct));
    }
}
