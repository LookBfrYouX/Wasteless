package com.navbara_pigeons.wasteless.service;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.navbara_pigeons.wasteless.entity.Currency;
import lombok.Data;
import org.springframework.stereotype.Service;

import java.io.*;
import java.net.URISyntaxException;
import java.util.HashMap;

@Service
public class CountryDataFetcherService {
    private final String resourcePath = "countryData.json";
    private HashMap<String, Currency> currencyHashMap;

    public CountryDataFetcherService() throws URISyntaxException, IOException {
        loadCountryData();
    }

    /**
     * Reads a file containing an API response from resources folder and generates the country hash map
     * @throws IOException
     */
    protected void loadCountryData() throws IOException
    {
        InputStream stream = this.getClass().getClassLoader().getResource("countryData.json").openStream();
        reloadCountryDataFromDisk(stream);
    }

    /**
     * Reads a file containing an API response from disk and generates the country hash map
     * @param file file containing to JSON response
     * @throws IOException
     */
    protected void reloadCountryDataFromDisk(InputStream file) throws IOException
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // JSON has lots of unnecessary properties that we can ignore
        Country[] countries = mapper.readValue(file, Country[].class);
        currencyHashMap = generateCurrencyHashMap(countries);
    }

    /**
     * Gets a hash map with the country name as key and the currency that should be used in that country
     * @return hash map
     */
    public HashMap<String, Currency> getCurrencyHashMap() {
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
            currencyHashMap.put(country.getName(), country.getCurrency());
        }

        return currencyHashMap;
    }

    /**
     * Checks if country exists (or if the REST Api returns that country and there is a currency we can use)
     * @param country name of the country
     * @return true if country is known
     */
    public boolean countryExists(String country) {
        return this.getCurrencyHashMap().containsKey(country);
    }

    /**
     * Gets the currency for the country
     * @param country name of the country
     * @return null if country not found, currency that should be used in the country otherwise
     */
    public Currency getCurrencyForCountry(String country) {
        return this.getCurrencyHashMap().get(country);
    }
}


@Data
class Country implements Serializable {
    private String name;
    private Currency currency;
}
