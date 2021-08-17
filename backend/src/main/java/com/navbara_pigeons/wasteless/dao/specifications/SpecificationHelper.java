package com.navbara_pigeons.wasteless.dao.specifications;

public class SpecificationHelper {
    public static boolean isFullMatching(String token) {
        return token.matches("\"\\S*.+?\"");
    }
}
