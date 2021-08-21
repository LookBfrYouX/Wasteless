package com.navbara_pigeons.wasteless.enums;


public enum ListingSearchKeys {
    PRODUCT_NAME("Product Name"),
    BUSINESS_NAME("Business Name"),
    ADDRESS("Address");
    
    String value;

    ListingSearchKeys(String value) {
        this.value = value;
    }

    /**
     * Converts string to ListingSearchKeys (using the enum value, not the name of the enum)
     *
     * @param ListingSearchKey string to convert
     * @return enum value
     * @throws IllegalArgumentException if invalid string given
     */
    public static ListingSearchKeys fromString(String ListingSearchKey) throws IllegalArgumentException {
        for (ListingSearchKeys b : ListingSearchKeys.values()) {
            if (b.value.equals(ListingSearchKey)) {
                return b;
            }
        }
        throw new IllegalArgumentException(
                String.format("Invalid search key given; got '%s'", ListingSearchKey));
    }

    /**
     * Returns string value of the business type
     */
    public String toString() {
        return this.value;
    }
}
