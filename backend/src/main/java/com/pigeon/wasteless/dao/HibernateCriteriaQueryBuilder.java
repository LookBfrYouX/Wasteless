package com.pigeon.wasteless.dao;

import com.pigeon.wasteless.entity.User;
import org.hibernate.Session;

import javax.management.InvalidAttributeValueException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HibernateCriteriaQueryBuilder {

    public static CriteriaQuery parseUserSearchQuery(Session currentSession, String searchQuery) throws InvalidAttributeValueException {
        // Setup stuff
        CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
        CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

        Root<User> root = criteriaQuery.from(User.class);
        criteriaQuery.select(root);

        // List of the predicates to filter by
        ArrayList<Predicate> predicates = new ArrayList<>();

        // Breaking down the searchQuery appropriately
        // -- currently just using spaces

        List<String> tokens = new ArrayList<String>();
        Matcher matcher = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(searchQuery);
        while (matcher.find()) {
            tokens.add(matcher.group(1));
        }

        for (int i = 0; i < tokens.size(); i++) {
            String currentToken = tokens.get(i);
            System.out.println(currentToken);
            if (currentToken.toUpperCase().matches("AND")) {
                System.out.println("AND found so just add the next search token: " + currentToken);
            } else if (currentToken.toUpperCase().matches("OR")) {
                // Check it is in a legit place
                if (i > 0 && (i + 1) < tokens.size() && predicates.size() > 0) {
                    System.out.println("legit AND found so check the next token exists and or it to a pop");
                    System.out.println("LENGTH: " + predicates.size());
                    Predicate lastPredicate = predicates.remove(predicates.size() - 1);
                    System.out.println("POP LENGTH: " + predicates.size());
                    Predicate newPredicate = criteriaBuilder.or(lastPredicate, makePredicate(tokens.get(i + 1), criteriaBuilder, root));
                    System.out.println(newPredicate.getExpressions());
                    predicates.add(newPredicate);
                    System.out.println("FINAL LENGTH: " + predicates.size());
                    i++; // Extra increment so the attribute isn't added again!
                } else {
                    throw new InvalidAttributeValueException("Check the AND syntax");
                }
            } else {
                System.out.println("Other token found: " + currentToken);
                predicates.add(makePredicate(currentToken, criteriaBuilder, root));
            }
        }

        // Checking for null predicates
        if (predicates.size() <= 0) {
            throw new InvalidAttributeValueException("Ya gots ta add'a co-rekt searchQuery bae");
        }

        // Selecting query
        criteriaQuery.select(root).where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

        // Returning the query
        return criteriaQuery;
    }

    private static Predicate makePredicate(String token, CriteriaBuilder criteriaBuilder, Root root) {
        System.out.println("Adding predicate for: " + token);
        if (token.matches("\"\\S*.+?\"")) {
            String newToken = token.replace("\"", "");
            return buildNameFullMatchPredicate(newToken, criteriaBuilder, root);
        } else {
            return buildNamePartialMatchPredicate(token, criteriaBuilder, root);
        }
    }

    private static Predicate buildNameFullMatchPredicate(String token, CriteriaBuilder criteriaBuilder, Root root) {
        return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), token.toLowerCase()),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nickname")), token.toLowerCase()),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("middleName")), token.toLowerCase()),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), token.toLowerCase())
        );
    }

    private static Predicate buildNamePartialMatchPredicate(String token, CriteriaBuilder criteriaBuilder, Root root) {
        return criteriaBuilder.or(
                criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), "%" + token.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("nickname")), "%" + token.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("middleName")), "%" + token.toLowerCase() + "%"),
                criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), "%" + token.toLowerCase() + "%")
        );
    }

}
