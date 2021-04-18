package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.service.CountryDataFetcherService;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;


@SpringBootTest
public class CountryDataFetcherServiceTest {
    @Autowired
    CountryDataFetcherService countryDataFetcherService;

    void load() {
        load("countryDataFetcherService/standard.json");
    }

    /**
     * Loads selected resource file as JSON for country data fetcher service
     * @param resourcePath path to resource
     */
    void load(String resourcePath) {
        try {
            Path path = Path.of(ClassLoader.getSystemClassLoader().getResource(resourcePath).toURI());
            countryDataFetcherService.reloadCountryDataFromDisk(path);
        } catch (IOException e) {
            e.printStackTrace();
            Assert.fail();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Assert.fail();
        }
    }

    @Test
    void resetCountryData() {
        load();
        Assert.assertNotNull(countryDataFetcherService.getCurrencyForCountry("New Zealand"));
        countryDataFetcherService.resetCountryData();
        Assert.assertFalse(countryDataFetcherService.dataLoaded());
    }

    /**
     * Tests to see if illegal state exception thrown when no data loaded
     * @throws URISyntaxException
     */
    @Test
    void checkIllegalStateException() {
        countryDataFetcherService.resetCountryData();
        Assert.assertFalse(countryDataFetcherService.dataLoaded());
        Assert.assertThrows(IllegalStateException.class, () -> countryDataFetcherService.countryExists(""));
        Assert.assertThrows(IllegalStateException.class, () -> countryDataFetcherService.getCurrencyForCountry(""));
    }

    /**
     * Test if new file actually loaded
     * @throws URISyntaxException
     */
    @Test
    void reloadCountryFromDiskTest() {
        load();
        Assert.assertTrue(countryDataFetcherService.dataLoaded());
        Assert.assertTrue(countryDataFetcherService.countryExists("Valid Currency"));
    }

    /**
     * Tests to see if countries with no valid currencies are ignored
     * @throws URISyntaxException
     */
    @Test
    void checkBadCountriesIgnored() {
        load();
        Assert.assertFalse(countryDataFetcherService.countryExists("Null Currency"));
        Assert.assertFalse(countryDataFetcherService.countryExists("No Currencies"));
    }

    @Test
    void checkCurrenciesFirstInvalid() {
        load();
        Assert.assertEquals(
                new Currency("CODE2", "NAME2", "SYMBOL2"),
                countryDataFetcherService.getCurrencyForCountry("Two Currencies First Null")
        );
    }

    @Test
    void checkCurrenciesSecondInvalid() {
        load();
        Assert.assertEquals(
                new Currency("CODE1", "NAME1", "SYMBOL1"),
                countryDataFetcherService.getCurrencyForCountry("Two Currencies Second Null")
        );
    }

    @Test
    void checkCurrenciesNonExistentCountry() {
        load();
        Assert.assertNull(countryDataFetcherService.getCurrencyForCountry("blaaa"));
    }

    @Test
    void checkCurrenciesTwoCurrencies() {
        load();
        Assert.assertEquals(
                new Currency("CODE1", "NAME1", "SYMBOL1"),
                countryDataFetcherService.getCurrencyForCountry("Two Valid Currencies")

        );
    }

    @Test
    void checkCurrenciesStandard() {
        load();
        Assert.assertEquals(
                new Currency("CODE", "NAME", "SYMBOL"),
                countryDataFetcherService.getCurrencyForCountry("Valid Currency")
        );
    }

    @Test
    void checkCurrenciesNewZealand() {
        load();
        Assert.assertEquals(
                new Currency("NZD", "New Zealand dollar", "$"),
                countryDataFetcherService.getCurrencyForCountry("New Zealand")
        );
    }
}