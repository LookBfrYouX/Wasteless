package com.navbara_pigeons.wasteless.dao.specifications;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecificationHelper {
    public static boolean isFullMatching(String token) {
        return token.matches("\"\\S*.+?\"");
    }

    public static List<String> tokenize(String searchQuery) {
        List<String> tokens = new ArrayList<>();
        searchQuery = searchQuery.strip();
        System.out.println(searchQuery);
        Matcher matcher = Pattern.compile("(\"[^\"]+\")|([^\"\\s]+)").matcher(searchQuery);
        while (matcher.find()) {
            tokens.add(matcher.group(0));
        }
        return tokens;
    }
}
