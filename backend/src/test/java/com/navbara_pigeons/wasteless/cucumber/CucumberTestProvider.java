package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.controller.BusinessController;
import com.navbara_pigeons.wasteless.controller.ProductController;
import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.testprovider.MainTestProvider;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@CucumberContextConfiguration
public class CucumberTestProvider extends MainTestProvider {

    @Autowired
    protected UserController userController;

    @Autowired
    protected BusinessController businessController;

    @Autowired
    protected ProductController productController;

    Product makeProduct(String productName) {
        Product product = new Product();
        product.setName(productName)
                .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
                .setDescription("A test product.")
                .setRecommendedRetailPrice(20.25);
        return product;
    }

    /**
     * This test helper method creates and returns a test business to be used in cucumber tests
     * from the given parameters.
     * @return Business business
     */
    Business makeBusiness(String businessName) {
        Business business = new Business();
        business.setName(businessName)
                .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
                .setBusinessType("Non-profit organisation")
                .setAddress(makeAddress())
                .setDescription("some description");
        return business;
    }

    /**
     * This test helper method creates and returns a test user to be used in cucumber tests
     * from the given parameters.
     * @return user User
     */
    protected User makeUser(String email, String password, Boolean isAdmin) {
        User testUser = new User();
        Address address = new Address()
                .setStreetNumber("3/24")
                .setStreetName("Ilam Road")
                .setPostcode("90210")
                .setCity("Christchurch")
                .setRegion("Canterbury")
                .setCountry("New Zealand");

        // Create test user
        testUser.setFirstName("Tony")
                .setLastName("Last")
                .setMiddleName("Middle")
                .setNickname("Nick")
                .setEmail(email)
                .setPhoneNumber("+6412345678")
                .setDateOfBirth("2000-03-10")
                .setHomeAddress(address)
                .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
                .setRole(isAdmin ? "ROLE_ADMIN" : "ROLE_USER")
                .setPassword(encodePass(password));

        // Save user using DAO and retrieve by Email
        return testUser;
    }

    /**
     * This test helper method creates and returns a test address to be used in cucumber tests
     * from the given parameters.
     * @return address Address
     */
    Address makeAddress() {
        Address address = new Address();
        address.setStreetNumber("3/24")
                .setStreetName("Ilam Road")
                .setPostcode("90210")
                .setCity("Christchurch")
                .setRegion("Canterbury")
                .setCountry("New Zealand");
        return address;
    }

    /**
     * This helper method encodes a password using the BCrypt encoder.
     * @param password A plaintext password String to encode.
     * @return String The encoded password.
     */
    String encodePass(String password) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        return bCryptPasswordEncoder.encode(password);
    }

}
