package com.navbara_pigeons.wasteless.validation;

public class ValidationHelper {
    /**
     * Checks if the given string is null, empty, or contains whitespace only
     *
     * @param str value string to check
     * @return true if the given string is null, empty, or contains whitespace only
     */
    public static boolean isNullOrTrimmedEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

}
