package com.navbara_pigeons.wasteless.testprovider;

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

    @Autowired
    protected CountryDataFetcherService countryDataFetcherService;

    @BeforeEach
    void loadCountryData() throws IOException, URISyntaxException {
        loadDefaultCountryDataIfNotLoaded();
    }

    /**
     * Helper which loads default country data
     * @throws URISyntaxException
     * @throws IOException
     */
    protected void loadDefaultCountryData() throws URISyntaxException, IOException {
        countryDataFetcherService.reloadCountryDataFromDisk(
                Path.of(ClassLoader.getSystemClassLoader().getResource("countryDataFetcherService/standard.json").toURI())
        );
    }

    /**
     * Helper which loads default country data if there is no country data loaded
     * @throws URISyntaxException
     * @throws IOException
     */
    protected void loadDefaultCountryDataIfNotLoaded() throws URISyntaxException, IOException {
        if (!countryDataFetcherService.dataLoaded()) loadDefaultCountryData();
    }


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

}
