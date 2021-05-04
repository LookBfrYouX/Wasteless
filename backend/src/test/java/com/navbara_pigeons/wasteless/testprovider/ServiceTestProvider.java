package com.navbara_pigeons.wasteless.testprovider;

import com.navbara_pigeons.wasteless.dto.*;
import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.security.model.UserCredentials;
import com.navbara_pigeons.wasteless.service.*;
import org.junit.Before;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * This class provides the setup for Service level tests and provides various convenience methods.
 */
public class ServiceTestProvider extends MainTestProvider {

    @Autowired
    protected UserService userService;

    @Autowired
    protected BusinessService businessService;

    @Autowired
    protected ProductService productService;

    /**
     * Logs in with a default set of credentials
     */
    protected void loginWithCredentials() {
        loginWithCredentials("dnb36@uclive.ac.nz", "fun123");
    }

    /**
     * Logs in with the given credentials
     * @param email
     * @param password
     */
    protected void loginWithCredentials(String email, String password) {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setEmail(email);
        userCredentials.setPassword(password);
        try {
            userService.login(userCredentials);
        } catch (Exception e) {
            Assertions.fail(e);
        }
    }

    /**
     * Asserts that a business and its DTO are equal
     * @param business
     * @param businessDto
     */
    protected void assertBusinessEquals(Business business, FullBusinessDto businessDto) {
        assertEquals(business.getId(), businessDto.getId());
        assertEquals(business.getName(), businessDto.getName());
        assertEquals(business.getDescription(), businessDto.getDescription());
        assertEquals(business.getCreated(), businessDto.getCreated());
        assertEquals(business.getBusinessType(), businessDto.getBusinessType());
        assertAddressEquals(business.getAddress(), businessDto.getAddress());
        assertEquals(business.getPrimaryAdministratorId(), businessDto.getPrimaryAdministratorId());

        // admin list
        assertUsersList(business.getAdministrators(), businessDto.getAdministrators());
    }

    /**
     * Asserts that a business and its DTO are equal
     * @param business
     * @param businessDto
     */
    protected void assertBusinessEquals(Business business, BasicBusinessDto businessDto) {
        assertEquals(business.getId(), businessDto.getId());
        assertEquals(business.getName(), businessDto.getName());
        assertEquals(business.getDescription(), businessDto.getDescription());
        assertEquals(business.getCreated(), businessDto.getCreated());
        assertEquals(business.getBusinessType(), businessDto.getBusinessType());

        assertEquals(business.getPrimaryAdministratorId(), businessDto.getPrimaryAdministratorId());
        assertAddressEquals(business.getAddress(), businessDto.getAddress());
    }


    /**
     * Assert list of users matches given list. Order does not matter
     * @param users
     * @param userDtos
     */
    protected void assertUsersList(List<User> users, List<?> userDtos) {
        HashSet<Long> usersNotFoundYet = new HashSet<>();
        users.forEach(user -> usersNotFoundYet.add(user.getId()));

        for (Object userObject : userDtos) {
            long id = -1;
            if (userObject instanceof FullUserDto) {
                id = ((FullUserDto) userObject).getId();
            } else if (userObject instanceof BasicUserDto) {
                id = ((BasicUserDto) userObject).getId();
            } else fail("Not user DTO object");

            long finalId = id;
            Optional<User> expectedUsersDto = users.stream().filter(business -> business.getId() == finalId).findFirst();
            if (expectedUsersDto.isEmpty()) fail("Received user with id " + id + "; not in original list");
            User expectedUser = expectedUsersDto.get();

            if (userObject instanceof FullUserDto) assertUserEquals(expectedUser, (FullUserDto) userObject);
            else assertUserEquals(expectedUser, (BasicUserDto) userObject);
            usersNotFoundYet.remove(expectedUser.getId());
        }

        if (usersNotFoundYet.size() > 0) fail("Businesses that were in original list not found");
    }

    /**
     * Given list of businesses, checks if all business match. Order does not matter
     * @param businesses
     * @param businessDtos
     */
    protected void assertBusinessList(List<Business> businesses, List<?> businessDtos) {
        HashSet<Long> businessesNotFoundYet = new HashSet<>();
        businesses.forEach(business -> businessesNotFoundYet.add(business.getId()));

        for (Object businessObject : businessDtos) {
            long id = -1;
            if (businessObject instanceof FullBusinessDto) {
                id = ((FullBusinessDto) businessObject).getId();
            } else if (businessObject instanceof BasicBusinessDto) {
                id = ((BasicBusinessDto) businessObject).getId();
            } else fail("Not business DTO object");

            long finalId = id;
            Optional<Business> expectedBusinessOptional = businesses.stream().filter(business -> business.getId() == finalId).findFirst();
            if (expectedBusinessOptional.isEmpty()) fail("Received business with id " + id + "; not in original list");
            Business expectedBusiness = expectedBusinessOptional.get();

            if (businessObject instanceof FullBusinessDto) assertBusinessEquals(
                expectedBusiness, (FullBusinessDto) businessObject);
            else assertBusinessEquals(expectedBusiness, (BasicBusinessDto) businessObject);
            businessesNotFoundYet.remove(expectedBusiness.getId());
        }

        if (businessesNotFoundYet.size() > 0) fail("Businesses that were in original list not found");
    }

    /**
     * Asserts that a user matches a user DTO
     * @param user
     * @param userDto
     */
    protected void assertUserEquals(User user, FullUserDto userDto) {
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getMiddleName(), userDto.getMiddleName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getNickname(), userDto.getNickname());

        assertEquals(user.getBio(), userDto.getBio());
        assertEquals(user.getCreated(), userDto.getCreated());
        assertEquals(user.getRole(), userDto.getRole());

        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getDateOfBirth(), userDto.getDateOfBirth());
        assertEquals(user.getPhoneNumber(), userDto.getPhoneNumber());

        assertAddressEquals(user.getHomeAddress(), userDto.getHomeAddress());

        assertBusinessList(user.getBusinesses(), userDto.getBusinesses());
    }

    /**
     * Asserts that a user matches a user DTO
     * @param user
     * @param userDto
     */
    protected void assertUserEquals(User user, BasicUserDto userDto) {
        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getFirstName(), userDto.getFirstName());
        assertEquals(user.getMiddleName(), userDto.getMiddleName());
        assertEquals(user.getLastName(), userDto.getLastName());
        assertEquals(user.getNickname(), userDto.getNickname());

        assertEquals(user.getBio(), userDto.getBio());
        assertEquals(user.getCreated(), userDto.getCreated());
        assertEquals(user.getRole(), userDto.getRole());

        assertAddressEquals(user.getHomeAddress(), userDto.getHomeAddress());

        assertBusinessList(user.getBusinesses(), userDto.getBusinesses());
    }

    /**
     * Asserts an address matches its DTO
     * @param address
     * @param addressDto
     */
    protected void assertAddressEquals(Address address, BasicAddressDto addressDto) {
        assertEquals(address.getCity(), addressDto.getCity());
        assertEquals(address.getRegion(), addressDto.getRegion());
        assertEquals(address.getCountry(), addressDto.getCountry());
    }

    /**
     * Asserts an address matches its DTO
     * @param address
     * @param addressDto
     */
    protected void assertAddressEquals(Address address, FullAddressDto addressDto) {
        assertEquals(address.getStreetNumber(), addressDto.getStreetNumber());
        assertEquals(address.getStreetName(), addressDto.getStreetName());
        assertEquals(address.getPostcode(), addressDto.getPostcode());
        assertEquals(address.getCity(), addressDto.getCity());
        assertEquals(address.getRegion(), addressDto.getRegion());
        assertEquals(address.getCountry(), addressDto.getCountry());
    }

}
