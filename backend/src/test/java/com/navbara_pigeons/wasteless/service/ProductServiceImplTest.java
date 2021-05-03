//package com.navbara_pigeons.wasteless.service;
//
//import com.navbara_pigeons.wasteless.entity.Business;
//import com.navbara_pigeons.wasteless.entity.Product;
//import com.navbara_pigeons.wasteless.exception.*;
//import com.navbara_pigeons.wasteless.testprovider.ServiceTestProvider;
//import net.minidev.json.JSONObject;
//import org.junit.BeforeClass;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Disabled;
//import org.junit.jupiter.api.Test;
//import org.springframework.security.test.context.support.WithUserDetails;
//
//import javax.transaction.Transactional;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class ProductServiceImplTest extends ServiceTestProvider {
//
//    @Test
//    @Transactional
//    @WithUserDetails(value = "mbi47@uclive.ac.nz")
//    void getProductsTest() {
//        try {
//            // Set up business and product
//            String testProdName = "testProd";
//            Business mockBusiness = makeBusiness("testBusiness");
//            Product mockProduct = makeProduct(testProdName);
//            mockBusiness.addCatalogueProduct(mockProduct);
//            // Save and retrieve the business
//            businessService.saveBusiness(mockBusiness);
//            List<Product> products = productService.getProducts(Long.toString(mockBusiness.getId()));
//            // Assert the business has at least one product
//            assertTrue(products.size() > 0);
//            // Assert the business has exactly the one product we added to it
//            assertTrue(products.size() == 1);
//            // Assert the product has the same name as the one we saved
//            assertTrue(products.get(0).getName().equals(testProdName));
//
//            // Fail test if any unexpected exceptions occur
//        } catch (Exception exc) {
//            exc.printStackTrace();
//            assertTrue(false);
//        }
//    }
//
//    Product findProductWithName(long businessId, String productName) throws BusinessNotFoundException, UserNotFoundException, InsufficientPrivilegesException {
//        Product savedProduct = null;
//        for(Product product: productService.getProducts(String.valueOf(businessId))) {
//            if (product.getName() == productName) {
//                // Find product with the same name
//                savedProduct = product;
//                break;
//            }
//        }
//
//        assertNotNull(savedProduct, "Couldn't find saved product");
//        return savedProduct;
//    }
//
//    @Test
//    @Transactional
//    @WithUserDetails(value="dnb36@uclive.ac.nz")
//    void createProductExpectOk() throws UserNotFoundException, BusinessTypeException, BusinessRegistrationException, InsufficientPrivilegesException, BusinessNotFoundException, AddressValidationException {
//        JSONObject mockProduct = new JSONObject();
//        final String name = "Pizza";
//        final double price = 9.99;
//        final String description = "DESCRIPTION";
//
//        Business mockBusiness = makeBusiness("testBusiness");
//        businessService.saveBusiness(mockBusiness);
//
//        mockProduct.put("name", name);
//        mockProduct.put("recommendedRetailPrice", price);
//        mockProduct.put("description", description);
//
//        assertDoesNotThrow(() -> productService.addProduct(mockBusiness.getId(), mockProduct));
//
//        Product savedProduct = findProductWithName(mockBusiness.getId(), name);
//        assertEquals(name, savedProduct.getName());
//        assertEquals("NZD", savedProduct.getCurrency());
//        assertEquals(price, savedProduct.getRecommendedRetailPrice());
//        assertEquals(description, savedProduct.getDescription());
//        // Delta between date created and current time less than a second
//        assertTrue(Math.abs(savedProduct.getCreated().toInstant().toEpochMilli() - System.currentTimeMillis()) < 1000);
//    }
//
//    @Test
//    @Transactional
//    @WithUserDetails(value="dnb36@uclive.ac.nz")
//    void createProductExpectException() {
//        JSONObject mockProduct = new JSONObject();
//        mockProduct.put("name", null);
//
//        assertThrows(Exception.class, () -> productService.addProduct(1, mockProduct));
//    }
//
//    @Test
//    @Transactional
//    @WithUserDetails(value="dnb36@uclive.ac.nz")
//    void createProductExpectInCatalogue() {
//        try {
//            String productName = "Pizza";
//            JSONObject mockProduct = new JSONObject();
//            mockProduct.put("name", productName);
//            Business mockBusiness = makeBusiness("testBusiness");
//            businessService.saveBusiness(mockBusiness);
//            assertDoesNotThrow(() -> productService.addProduct(mockBusiness.getId(), mockProduct));
//            assertEquals(mockBusiness.getProductsCatalogue().get(0).getName(), productName);
//            // Fail test if any unexpected exceptions occur
//        } catch (Exception exc) {
//            exc.printStackTrace();
//            assertTrue(false);
//        }
//    }
//}
//
