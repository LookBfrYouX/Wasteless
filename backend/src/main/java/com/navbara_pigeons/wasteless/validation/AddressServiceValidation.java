package com.navbara_pigeons.wasteless.validation;

import com.navbara_pigeons.wasteless.entity.Address;

public class AddressServiceValidation {
    /**
     * Returns false if required address fields are null or empty.
     * DOES NOT CHECK IF COUNTRY IS VALID
     * @param address address to validate
     */
    public static boolean requiredFieldsNotEmpty(Address address) {
        // Checks user fields are not null/empty
        if (ValidationHelper.isNullOrTrimmedEmpty(address.getCountry())) {
            return false;
        }

        return true;
    }

}
