package com.navbara_pigeons.wasteless.dao.HibernateQueryBuilders;

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

public class UserQueryBuilder {

  private UserQueryBuilder() {
  }

  /**
   * Create a query for retrieving the total count of the clients query
   *
   * @param currentSession The Session
   * @param searchQuery    The search query for the group of users
   * @return A Query for retrieving the total count (Long) of the clients query
   */
  public static CriteriaQuery<Long> createTotalUserCountQuery(Session currentSession,
      String searchQuery)
      throws InvalidAttributeValueException {
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<User> criteriaQuery = parseUserSearchQuery(currentSession, searchQuery);
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    Root<User> root2 = countQuery.from(criteriaQuery.getResultType());
    root2.alias("userAlias");
    countQuery.select(criteriaBuilder.count(root2));
    Predicate restriction = criteriaQuery.getRestriction();
    if (restriction != null) {
      countQuery.where(restriction); // Copy restrictions
    }
    return countQuery;
  }

  /**
   * Create a query to return a list of paginated and sorted Users that match the search criteria.
   *
   * @param currentSession The Session
   * @param searchQuery    The search query for the group of users
   * @param pagBuilder     The pagination builder that holds all the clients pagination values
   * @return A Query that returns a list of paginated and sorted Users
   * @throws InvalidAttributeValueException
   */
  public static TypedQuery<User> listPaginatedAndSortedUsers(Session currentSession,
      String searchQuery, PaginationBuilder pagBuilder)
      throws InvalidAttributeValueException {
    CriteriaQuery<User> criteriaQuery = parseUserSearchQuery(currentSession, searchQuery);
    return userPaginationAndSorting(criteriaQuery, currentSession, pagBuilder);
  }

  private static CriteriaQuery<User> parseUserSearchQuery(Session currentSession,
      String searchQuery) throws InvalidAttributeValueException {
    // Setup
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);

    Root<User> root = criteriaQuery.from(User.class);
    root.alias("userAlias");
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
                  lastPredicate,
                  makePredicateForUserQuery(tokens.get(i + 1), criteriaBuilder, root));
          predicates.add(newPredicate);
          i++; // Extra increment so the attribute isn't added again!
        } else {
          throw new InvalidAttributeValueException("Check the AND syntax");
        }
      } else {
        predicates.add(makePredicateForUserQuery(currentToken, criteriaBuilder, root));
      }
    }

    // Checking for null predicates
    if (predicates.size() <= 0 && !searchQuery.equals("")) {
      throw new InvalidAttributeValueException("No search query tokens found");
    }

    // Selecting query
    criteriaQuery.select(root).where(criteriaBuilder.and(predicates.toArray(new Predicate[0])));

    return criteriaQuery;
  }

  /**
   * Order and sort the search query
   *
   * @param criteriaQuery  The query to order/sort
   * @param currentSession The Session
   * @param pagBuilder     The pagination builder that holds all the clients pagination values
   * @return A Query that returns a list of paginated and sorted Users
   */
  private static TypedQuery<User> userPaginationAndSorting(CriteriaQuery<User> criteriaQuery,
      Session currentSession, PaginationBuilder pagBuilder) {
    // Setup
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    Root<User> root = criteriaBuilder.createQuery(User.class).from(User.class);
    root.alias("userAlias");

    // Sorting query
    Path<Object> path = root.get(pagBuilder.getSortField().toString());
    Order order =
        pagBuilder.isSortAscending() ? criteriaBuilder.asc(path) : criteriaBuilder.desc(path);
    criteriaQuery.orderBy(order);

    // Paginating query
    TypedQuery<User> typedQuery = currentSession.createQuery(criteriaQuery);
    typedQuery.setFirstResult(pagBuilder.getPagStartIndex());
    if (pagBuilder.getPagEndIndex() != null) {
      typedQuery.setMaxResults(pagBuilder.getPagEndIndex() - pagBuilder.getPagStartIndex() + 1);
    }
    return typedQuery;
  }

  private static Predicate makePredicateForUserQuery(
      String token, CriteriaBuilder criteriaBuilder, Root<User> root) {
    if (token.matches("\"\\S*.+?\"")) {
      String newToken = token.replace("\"", "");
      return buildNameFullMatchPredicateForUserQuery(newToken, criteriaBuilder, root);
    } else {
      return buildNamePartialMatchPredicateForUserQuery(token, criteriaBuilder, root);
    }
  }

  private static Predicate buildNameFullMatchPredicateForUserQuery(
      String token, CriteriaBuilder criteriaBuilder, Root<User> root) {
    return criteriaBuilder.or(
        criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName")), token.toLowerCase()),
        criteriaBuilder.like(criteriaBuilder.lower(root.get("nickname")), token.toLowerCase()),
        criteriaBuilder.like(criteriaBuilder.lower(root.get("middleName")), token.toLowerCase()),
        criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName")), token.toLowerCase()));
  }

  private static Predicate buildNamePartialMatchPredicateForUserQuery(
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