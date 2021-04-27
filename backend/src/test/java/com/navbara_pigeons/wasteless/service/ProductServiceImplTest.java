package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.BusinessRegistrationException;
import com.navbara_pigeons.wasteless.exception.BusinessTypeException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.transaction.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProductServiceImplTest extends ServiceTestProvider {

    @Test
    @Transactional
    @WithUserDetails(value = "mbi47@uclive.ac.nz")
    void getProductsTest() {
        try {
            // Set up business and product
            String testProdName = "testProd";
            Business mockBusiness = makeBusiness("testBusiness");
            Product mockProduct = makeProduct(testProdName);
            mockBusiness.addCatalogueProduct(mockProduct);
            // Save and retrieve the business
            businessService.saveBusiness(mockBusiness);
            List<Product> products = productService.getProducts(Long.toString(mockBusiness.getId()));
            // Assert the business has at least one product
            assertTrue(products.size() > 0);
            // Assert the business has exactly the one product we added to it
            assertTrue(products.size() == 1);
            // Assert the product has the same name as the one we saved
            assertTrue(products.get(0).getName().equals(testProdName));

            // Fail test if any unexpected exceptions occur
        } catch (Exception exc) {
            exc.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    @Transactional
    @WithUserDetails(value="dnb36@uclive.ac.nz")
    void createProductExpectOk() {
        try {
            JSONObject mockProduct = new JSONObject();
            mockProduct.put("name", "Pizza");
            Business mockBusiness = makeBusiness("testBusiness");
            businessService.saveBusiness(mockBusiness);
            assertDoesNotThrow(() -> productService.addProduct(mockBusiness.getId(), mockProduct));
            // Fail test if any unexpected exceptions occur
        } catch (Exception exc) {
            exc.printStackTrace();
            assertTrue(false);
        }
    }

    @Test
    @Transactional
    @WithUserDetails(value="dnb36@uclive.ac.nz")
    void createProductExpectException() {
        JSONObject mockProduct = new JSONObject();
        mockProduct.put("name", null);

        assertThrows(Exception.class, () -> productService.addProduct(1, mockProduct));
    }

    @Test
    @Transactional
    @WithUserDetails(value="dnb36@uclive.ac.nz")
    void createProductExpectInCatalogue() {
        try {
            String productName = "Pizza";
            JSONObject mockProduct = new JSONObject();
            mockProduct.put("name", productName);
            Business mockBusiness = makeBusiness("testBusiness");
            businessService.saveBusiness(mockBusiness);
            assertDoesNotThrow(() -> productService.addProduct(mockBusiness.getId(), mockProduct));
            assertEquals(mockBusiness.getProductsCatalogue().get(0).getName(), productName);
            // Fail test if any unexpected exceptions occur
        } catch (Exception exc) {
            exc.printStackTrace();
            assertTrue(false);
        }
    }
}
