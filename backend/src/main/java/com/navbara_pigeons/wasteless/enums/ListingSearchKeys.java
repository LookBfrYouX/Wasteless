package com.navbara_pigeons.wasteless.enums;

public enum ListingSearchKeys {
    PRODUCT_NAME {
        @Override
        public String toString() {
            return "Product Name";
        }
    },
    BUSINESS_NAME {
        @Override
        public String toString() {
            return "Business Name";
        }
    },
    ADDRESS {
        @Override
        public String toString() {
            return "Address";
        }
    };
}
