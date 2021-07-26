package com.navbara_pigeons.wasteless.dao.HibernateQueryBuilders;

import com.navbara_pigeons.wasteless.entity.Address;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.enums.MarketListingSortByOption;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class MarketListingQueryBuilder {

  private MarketListingQueryBuilder() {
  }

  /**
   * Create a query for retrieving the total count of the clients query
   *
   * @param currentSession The Session
   * @param section        The section of the marketplace to query
   * @return A Query for retrieving the total count (Long) of the clients query
   */
  public static Query<Long> createTotalMarketListingsCountQuery(
      Session currentSession, String section) {
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    Root<MarketListing> marketListing = countQuery.from(MarketListing.class);
    countQuery.where(criteriaBuilder.equal(marketListing.get("section"), section));
    countQuery.select(criteriaBuilder.count(marketListing));

    return currentSession.createQuery(countQuery);
  }

  /**
   * Create a query to return a list of paginated and sorted Market Listings that match the search
   * criteria.
   *
   * @param currentSession The Session
   * @param section        The section of the marketplace to query
   * @param pagBuilder     The pagination builder that holds all the clients pagination values
   * @return A Query that returns a list of paginated and sorted Market Listings
   */
  public static TypedQuery<MarketListing> listPaginatedAndSortedMarketListings(
      Session currentSession, String section, PaginationBuilder pagBuilder) {
    // Setup
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<MarketListing> criteriaQuery = criteriaBuilder.createQuery(MarketListing.class);

    Root<MarketListing> root = criteriaQuery.from(MarketListing.class);
    Join<MarketListing, User> user = root.join("creator");
    Join<User, Address> address = user.join("homeAddress");
    criteriaQuery.where(criteriaBuilder.equal(root.get("section"), section));

    // Sorting query
    Path<Object> path;
    switch ((MarketListingSortByOption) pagBuilder.getSortField()) {
      case title:
      case created:
      case displayPeriodEnd:
        path = root.get(pagBuilder.getSortField().toString());
        break;
      case suburb:
      case city:
        path = address.get(pagBuilder.getSortField().toString());
        break;
      default:
        throw new IllegalStateException(
            "Unexpected value: " + pagBuilder.getSortField());
    }
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
}