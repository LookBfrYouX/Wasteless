package com.navbara_pigeons.wasteless.testprovider;

import com.navbara_pigeons.wasteless.service.CountryDataFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;

/**
 * This test helper class provides the general setup and methods available to all Service and Dao tests.
 * Test classes can extend this MainTestProvider to have access to the functionality.
 */
@SpringBootTest
public class MainTestProvider {

    @Autowired
    protected CountryDataFetcherService countryDataFetcherService;

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

}
