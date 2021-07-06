package com.navbara_pigeons.wasteless.dao;


import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.management.InvalidAttributeValueException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;


public class HibernateCriteriaQueryBuilder {

  private HibernateCriteriaQueryBuilder() {

  }

  private static Long getEntityCountQuery(Session currentSession, Object entity) {
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    countQuery.select(criteriaBuilder.count(countQuery.from((Class<?>) entity)));
    return currentSession.createQuery(countQuery).getSingleResult();
  }

  public static CriteriaQuery<MarketListing> parseListingQuery(Session currentSession,
      String listingSection) {
    // Create Builder
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<MarketListing> criteriaQuery = criteriaBuilder.createQuery(MarketListing.class);
    // Setup
    Root<MarketListing> root = criteriaQuery.from(MarketListing.class);
    criteriaQuery.select(root);
    // Create query - uses entity 'section'
    criteriaQuery.select(root).where(criteriaBuilder.equal(root.get("section"), listingSection));
    return criteriaQuery;
  }

  public static TypedQuery<InventoryItem> listPaginatedAndSortedBusinessInventory(
      Session currentSession, Business business, PaginationBuilder pagBuilder) {
    // Setup
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<InventoryItem> criteriaQuery = criteriaBuilder.createQuery(InventoryItem.class);

    Root<InventoryItem> root = criteriaQuery.from(InventoryItem.class);

    criteriaQuery.where(criteriaBuilder.equal(root.get("business"), business));

    // Sorting query
    Path<Object> path = root.get(pagBuilder.getSortField());
    Order order =
        pagBuilder.isSortAscending() ? criteriaBuilder.asc(path) : criteriaBuilder.desc(path);
    criteriaQuery.orderBy(order);

    TypedQuery<InventoryItem> typedQuery = currentSession.createQuery(criteriaQuery);
    typedQuery.setFirstResult(pagBuilder.getPagStartIndex());
    if (pagBuilder.getPagEndIndex() != null) {
      typedQuery.setMaxResults(pagBuilder.getPagEndIndex() - pagBuilder.getPagStartIndex() + 1);
    }
    return typedQuery;
  }

  public static TypedQuery<User> parseUserSearchQuery(Session currentSession, String searchQuery,
      PaginationBuilder pagBuilder) throws InvalidAttributeValueException {

    // Setup
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

    Root<User> root = criteriaQuery.from(User.class);
    criteriaQuery.select(root);

    // List of the predicates to filter by
    ArrayList<Predicate> predicates = new ArrayList<>();

    // Breaking down the searchQuery appropriately
    // -- currently just using spaces

    List<String> tokens = new ArrayList<>();
    Matcher matcher = Pattern.compile("(\"[^\"]+\")|([^\"\\s]+)").matcher(searchQuery);
    while (matcher.find()) {
      tokens.add(matcher.group(0));
    }

    for (int i = 0; i < tokens.size(); i++) {
      String currentToken = tokens.get(i);
      if (currentToken.toUpperCase().matches("AND")) {
        // AND is default join for predicates so this is empty
      } else if (currentToken.toUpperCase().matches("OR")) {
        // Check it is in a legit place
        if (i > 0 && (i + 1) < tokens.size() && !predicates.isEmpty()) {
          Predicate lastPredicate = predicates.remove(predicates.size() - 1);
          Predicate newPredicate = criteriaBuilder
              .or(lastPredicate, makePredicate(tokens.get(i + 1), criteriaBuilder, root));
          predicates.add(newPredicate);
          i++; // Extra increment so the attribute isn't added again!
        } else {
          throw new InvalidAttributeValueException("Check the AND syntax");
        }
      } else {
        predicates.add(makePredicate(currentToken, criteriaBuilder, root));
      }
    }

    // Checking for null predicates
    if (predicates.size() <= 0 && !searchQuery.equals("")) {
      throw new InvalidAttributeValueException("No search query tokens found");
    }

    // Selecting query
    criteriaQuery.select(root)
        .where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

    // Sorting query
    Path<Object> path = root.get(pagBuilder.getSortField());
    Order order =
        pagBuilder.isSortAscending() ? criteriaBuilder.asc(path) : criteriaBuilder.desc(path);
    criteriaQuery.orderBy(order);

    TypedQuery<User> typedQuery = currentSession.createQuery(criteriaQuery);
    typedQuery.setFirstResult(pagBuilder.getPagStartIndex());
    if (pagBuilder.getPagEndIndex() != null) {
      typedQuery.setMaxResults(pagBuilder.getPagEndIndex() - pagBuilder.getPagStartIndex() + 1);
    }
    return typedQuery;
  }

  private static Predicate makePredicate(String token, CriteriaBuilder criteriaBuilder,
      Root<User> root) {
    if (token.matches("\"\\S*.+?\"")) {
      String newToken = token.replace("\"", "");
      return buildNameFullMatchPredicate(newToken, criteriaBuilder, root);
    } else {
      return buildNamePartialMatchPredicate(token, criteriaBuilder, root);
    }
  }

  private static Predicate buildNameFullMatchPredicate(String token,
      CriteriaBuilder criteriaBuilder, Root<User> root) {
    return criteriaBuilder.or(
        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), token.toLowerCase()),
        criteriaBuilder.like(criteriaBuilder.lower(root.get("nickname")), token.toLowerCase()),
        criteriaBuilder.like(criteriaBuilder.lower(root.get("middleName")), token.toLowerCase()),
        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), token.toLowerCase())
    );
  }

  private static Predicate buildNamePartialMatchPredicate(String token,
      CriteriaBuilder criteriaBuilder, Root<User> root) {
    return criteriaBuilder.or(
        criteriaBuilder
            .like(criteriaBuilder.lower(root.get("firstName")), "%" + token.toLowerCase() + "%"),
        criteriaBuilder
            .like(criteriaBuilder.lower(root.get("nickname")), "%" + token.toLowerCase() + "%"),
        criteriaBuilder
            .like(criteriaBuilder.lower(root.get("middleName")), "%" + token.toLowerCase() + "%"),
        criteriaBuilder
            .like(criteriaBuilder.lower(root.get("lastName")), "%" + token.toLowerCase() + "%")
    );
  }

}
