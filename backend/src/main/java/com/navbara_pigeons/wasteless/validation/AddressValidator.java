package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.service.CountryDataFetcherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressValidator {
    private final CountryDataFetcherService countryDataFetcherService;

    @Autowired
    public AddressValidator(CountryDataFetcherService countryDataFetcherService) {
        this.countryDataFetcherService = countryDataFetcherService;
    }

    /**
     * Returns false if required address fields are null or empty.
     * Does not check if country is valid.
     * @param address address to validate
     */
    public boolean isAddressValid(Address address) {
        // Checks user fields are not null/empty
        if (UserServiceValidation.isNullOrTrimmedEmpty(address.getCountry())) {
            return false;
        }

        return true;
    }

    /**
     * Checks if the country is valid
     * @param country name of country
     * @return null if cannot check, true if country known
     */
    public Boolean isCountryValid(String country) {
        if (!this.countryDataFetcherService.dataLoaded()) return null;
        return this.countryDataFetcherService.countryExists(country);
    }
}
