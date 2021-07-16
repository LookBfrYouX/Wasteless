package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.entity.Product;
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
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class HibernateCriteriaQueryBuilder {

  private HibernateCriteriaQueryBuilder() {}

  public static Long getEntityCountQuery(Session currentSession, Object entity) {
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    countQuery.select(criteriaBuilder.count(countQuery.from((Class<?>) entity)));
    return currentSession.createQuery(countQuery).getSingleResult();
  }

  public static Query<Long> createTotalMarketListingsCountQuery(
      Session currentSession, String section) {
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    Root<MarketListing> marketListing = countQuery.from(MarketListing.class);
    countQuery.where(criteriaBuilder.equal(marketListing.get("section"), section));
    countQuery.select(criteriaBuilder.count(marketListing));

    return currentSession.createQuery(countQuery);
  }

  public static Query<Long> createTotalListingsCountQuery(
      Session currentSession, Business business) {
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    Root<Listing> listing = countQuery.from(Listing.class);
    Join<Listing, InventoryItem> inventoryItem = listing.join("inventoryItem");
    countQuery.where(criteriaBuilder.equal(inventoryItem.get("business"), business));
    countQuery.select(criteriaBuilder.count(inventoryItem));

    return currentSession.createQuery(countQuery);
  }

  public static TypedQuery<Listing> listPaginatedAndSortedBusinessListings(
      Session currentSession, Business business, PaginationBuilder pagBuilder) {
    // Setup
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();

    // Listings Query
    CriteriaQuery<Listing> criteriaQuery = criteriaBuilder.createQuery(Listing.class);
    Root<Listing> listing = criteriaQuery.from(Listing.class);
    Join<Listing, InventoryItem> inventoryItem = listing.join("inventoryItem");
    criteriaQuery.where(criteriaBuilder.equal(inventoryItem.get("business"), business));

    // Sorting query
    Path<Object> path = listing.get(pagBuilder.getSortField());
    Order order =
        pagBuilder.isSortAscending() ? criteriaBuilder.asc(path) : criteriaBuilder.desc(path);
    criteriaQuery.orderBy(order);

    // Pagination
    TypedQuery<Listing> typedQuery = currentSession.createQuery(criteriaQuery);
    typedQuery.setFirstResult(pagBuilder.getPagStartIndex());
    if (pagBuilder.getPagEndIndex() != null) {
      typedQuery.setMaxResults(pagBuilder.getPagEndIndex() - pagBuilder.getPagStartIndex() + 1);
    }
    return typedQuery;
  }

  public static TypedQuery<Product> listPaginatedAndSortedBusinessProducts(
      Session currentSession, Business business, PaginationBuilder pagBuilder) {
    // Setup
    String HQL =
        "SELECT bpc FROM Business b JOIN b.productsCatalogue bpc WHERE b.id=:business_id ORDER BY bpc."
            + pagBuilder.getSortField();
    HQL += pagBuilder.isSortAscending() ? " ASC" : " DESC";
    TypedQuery<Product> typedQuery =
        currentSession
            .createQuery(HQL, Product.class)
            .setParameter("business_id", business.getId());
    typedQuery.setFirstResult(pagBuilder.getPagStartIndex());
    if (pagBuilder.getPagEndIndex() != null) {
      typedQuery.setMaxResults(pagBuilder.getPagEndIndex() - pagBuilder.getPagStartIndex() + 1);
    }
    return typedQuery;
  }

  public static TypedQuery<MarketListing> listPaginatedAndSortedMarketListings(
      Session currentSession, String section, PaginationBuilder pagBuilder) {
    // Setup
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<MarketListing> criteriaQuery = criteriaBuilder.createQuery(MarketListing.class);

    Root<MarketListing> root = criteriaQuery.from(MarketListing.class);
    criteriaQuery.where(criteriaBuilder.equal(root.get("section"), section));

    // Sorting query
    Path<Object> path = root.get(pagBuilder.getSortField());
    Order order =
        pagBuilder.isSortAscending() ? criteriaBuilder.asc(path) : criteriaBuilder.desc(path);
    criteriaQuery.orderBy(order);

    TypedQuery<MarketListing> typedQuery = currentSession.createQuery(criteriaQuery);
    typedQuery.setFirstResult(pagBuilder.getPagStartIndex());
    if (pagBuilder.getPagEndIndex() != null) {
      typedQuery.setMaxResults(pagBuilder.getPagEndIndex() - pagBuilder.getPagStartIndex() + 1);
    }
    return typedQuery;
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

  public static TypedQuery<User> parseUserSearchQuery(
      Session currentSession, String searchQuery, PaginationBuilder pagBuilder)
      throws InvalidAttributeValueException {

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
          Predicate newPredicate =
              criteriaBuilder.or(
                  lastPredicate, makePredicate(tokens.get(i + 1), criteriaBuilder, root));
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
    criteriaQuery.select(root).where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

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

  private static Predicate makePredicate(
      String token, CriteriaBuilder criteriaBuilder, Root<User> root) {
    if (token.matches("\"\\S*.+?\"")) {
      String newToken = token.replace("\"", "");
      return buildNameFullMatchPredicate(newToken, criteriaBuilder, root);
    } else {
      return buildNamePartialMatchPredicate(token, criteriaBuilder, root);
    }
  }

  private static Predicate buildNameFullMatchPredicate(
      String token, CriteriaBuilder criteriaBuilder, Root<User> root) {
    return criteriaBuilder.or(
        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), token.toLowerCase()),
        criteriaBuilder.like(criteriaBuilder.lower(root.get("nickname")), token.toLowerCase()),
        criteriaBuilder.like(criteriaBuilder.lower(root.get("middleName")), token.toLowerCase()),
        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), token.toLowerCase()));
  }

  private static Predicate buildNamePartialMatchPredicate(
      String token, CriteriaBuilder criteriaBuilder, Root<User> root) {
    return criteriaBuilder.or(
        criteriaBuilder.like(
            criteriaBuilder.lower(root.get("firstName")), "%" + token.toLowerCase() + "%"),
        criteriaBuilder.like(
            criteriaBuilder.lower(root.get("nickname")), "%" + token.toLowerCase() + "%"),
        criteriaBuilder.like(
            criteriaBuilder.lower(root.get("middleName")), "%" + token.toLowerCase() + "%"),
        criteriaBuilder.like(
            criteriaBuilder.lower(root.get("lastName")), "%" + token.toLowerCase() + "%"));
  }
}