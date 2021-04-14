package com.navbara_pigeons.wasteless.validation;

import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.Serializable;
import java.util.HashMap;

@Service
public class CountryDataFetcherService {
    private final RestTemplate restTemplate;
    private final String requestPath = "https://restcountries.eu/rest/v2/all";
    private Country[] countries = null;
    private HashMap<String, Currency> currencyHashMap;

    @Autowired
    public CountryDataFetcherService(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    /**
     * Gets a list of country **objects** (not country name)
     * @return list of country objects
     */
    public Country[] getCountries() {
        if (this.countries != null) return this.countries;

        this.fetchCountries();
        // Following https://attacomsian.com/blog/http-requests-resttemplate-spring-boot
        return this.countries;
    }

    /**
     * Fetches countries from the API and sets this.countries
     */
    private void fetchCountries() {
        this.countries = this.restTemplate.getForObject(this.requestPath, Country[].class);
    }

    /**
     * Gets a hash map with the country name as key and the currency that should be used in that country
     * @return hash map
     */
    public HashMap<String, Currency> getCurrencyHashMap() {
        if (this.currencyHashMap != null) return this.currencyHashMap;
        else this.generateCurrencyHashMap(this.getCountries());
        return this.currencyHashMap;
    }

    /**
     * Generates hash map between country name and the currency that should be used in that country,
     * setting this.currencyHashMap
     */
    private void generateCurrencyHashMap(Country[] countries) {
        this.currencyHashMap = new HashMap<>();
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
            this.currencyHashMap.put(country.getName(), currencyToUse);
        }
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
     * @returnn null if country not found, currency that should be used in the country otherwise
     */
    public Currency getCurrencyForCountry(String country) {
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