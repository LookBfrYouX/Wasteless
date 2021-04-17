package com.navbara_pigeons.wasteless.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;

// TODO add minimum delay between requests?
// TODO business service not validating address
@Service
public class CountryDataFetcherService {
    private final RestTemplate restTemplate;
    private final String requestPath = "https://restcountries.eu/rest/v2/all";
    private final String fileName = "rest-countries-country-info.json";
    private HashMap<String, Currency> currencyHashMap;

    public CountryDataFetcherService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Calls API and saves the response to disk, to the default path
     * @throws Exception file does not exist, no internet connection etc.
     * Exceptions other than IOException don't need to be declared so just throwing exception instead of a list of specific errors
     */
    public void callAndSaveApiResponse() throws Exception {
        callAndSaveApiResponse(getDefaultJsonPath());
    }

    /**
     * Calls API and saves the response to disk
     * @param path path to save the file to; will overwrite
     * @throws Exception file does not exist, no internet connection etc.
     * Exceptions other than IOException don't need to be declared so just throwing exception instead of a list of specific errors
     */
    public void callAndSaveApiResponse(Path path) throws Exception {
        InputStream stream = null;
        try {
            stream = URI.create(requestPath).toURL().openStream();
            Files.copy(stream, path, StandardCopyOption.REPLACE_EXISTING);
        } finally {
            if (stream != null) stream.close();
        }
    }

    /**
     * Get the default path where the API response saved
     * @return default path for API response
     */
    public Path getDefaultJsonPath() {
        return Paths.get("./" + fileName);
    }

    /**
     * Reads default API response file from disk and generates the country hash map
     * @throws IOException
     */
    public void reloadCountryDataFromDisk() throws IOException {
        reloadCountryDataFromDisk(getDefaultJsonPath());
    }

    /**
     * Reads a file containing an API response from disk and generates the country hash map
     * @param path path to JSON response
     * @throws IOException
     */
    public void reloadCountryDataFromDisk(Path path) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // JSON has lots of unnecessary properties that we can ignore
        Country[] countries = mapper.readValue(path.toFile(), Country[].class);
        currencyHashMap = generateCurrencyHashMap(countries);
    }

    /**
     * Checks if country/currency data has been loaded into this instance
     * @return true if currency hash map exists
     */
    public boolean dataLoaded() {
        return this.currencyHashMap != null;
    }

    /**
     * Gets a hash map with the country name as key and the currency that should be used in that country
     * @return hash map
     * @throws IllegalStateException if currency hash map has not been generated
     */
    public HashMap<String, Currency> getCurrencyHashMap() throws IllegalStateException {
        if (this.currencyHashMap == null) throw new IllegalStateException("Currency hash map has not been generated yet");
        return this.currencyHashMap;
    }

    /**
     * Generates hash map between country name and the currency that should be used in that country
     * @param countries array of country objects
     * @return hash map
     */
    private HashMap<String, Currency> generateCurrencyHashMap(Country[] countries) {
        HashMap<String, Currency> currencyHashMap = new HashMap<>();
        for (Country country: countries) {
            Currency currencyToUse = null;

            // Use the first currency where the code is not null
            for(Currency currency: country.getCurrencies()) {
                if (currency.getCode() != null) {
                    currencyToUse = currency;
                    break;
                }
            }

            if (currencyToUse == null) continue; // No valid currency found
            currencyHashMap.put(country.getName(), currencyToUse);
        }

        return currencyHashMap;
    }

    /**
     * Checks if country exists (or if the REST Api returns that country and there is a currency we can use)
     * @param country name of the country
     * @return true if country is known
     * @throws IllegalStateException if called before currency hash map generated
     */
    public boolean countryExists(String country) throws IllegalStateException {
        return this.getCurrencyHashMap().containsKey(country);
    }

    /**
     * Gets the currency for the country
     * @param country name of the country
     * @return null if country not found, currency that should be used in the country otherwise
     * @throws IllegalStateException if called before currency hash map generated
     */
    public Currency getCurrencyForCountry(String country) throws IllegalStateException {
        return this.getCurrencyHashMap().get(country);
    }
}


@Data
class Country implements Serializable {
    private String name;
    private Currency[] currencies;
}

@Data
class Currency implements Serializable {
    private String code; // Use as ID
    private String name;
    private String symbol;

    @Override
    public String toString() {
        return String.format("%s (%s, %s)", name, code, symbol);
    }
}