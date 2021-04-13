package com.navbara_pigeons.wasteless.cucumber;

import com.navbara_pigeons.wasteless.controller.UserController;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@CucumberContextConfiguration
@SpringBootTest
public class SpringIntegrationTest {

    @Autowired
    UserController userController;

    protected void login(UserCredentials userCredentials) {
        userController.login(userCredentials);
    }

    protected void registerUser(User user) {
        userController.registerUser(user);
    }

    /**
     * This test helper method creates and returns a test business to be used in cucumber tests
     * from the given parameters.
     * @return Business business
     */
    Business makeBusiness() {
        Business business = new Business();
        business.setName("test")
                .setCreated(ZonedDateTime.now(ZoneOffset.UTC))
                .setBusinessType("Non-profit organisation")
                .setAddress(makeAddress())
                .setId(0)
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
        testUser.setId(0)
                .setFirstName("Tony")
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
