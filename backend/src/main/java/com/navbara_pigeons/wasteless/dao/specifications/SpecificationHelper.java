package com.navbara_pigeons.wasteless.dao.specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecificationHelper {
    // matches characters between quotes.
    public static boolean isFullMatching(String token) {
        return token.matches("\"\\S*.+?\"");
    }

    /**
     * Method to create tokens from search query
     * @param searchQuery query to tokenize
     */
    public static List<String> tokenize(String searchQuery) {
        List<String> tokens = new ArrayList<>();
        if (searchQuery == null) {
            return tokens;
        }

        searchQuery = searchQuery.strip();
        // Matches words
        Matcher matcher = Pattern.compile("(\"[^\"]+\")|([^\"\\s]+)").matcher(searchQuery);
        while (matcher.find()) {
            tokens.add(matcher.group(0));
        }
        return tokens;
    }
}
