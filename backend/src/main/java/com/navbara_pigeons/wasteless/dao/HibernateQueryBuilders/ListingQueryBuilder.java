package com.navbara_pigeons.wasteless.dao.HibernateQueryBuilders;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.enums.ListingSortByOption;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
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

public class ListingQueryBuilder {

  private ListingQueryBuilder() {
  }

  /**
   * Create a query for retrieving the total count of the clients query
   *
   * @param currentSession The Session
   * @param business       The business to query for the listings
   * @return A Query for retrieving the total count (Long) of the clients query
   */
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

  /**
   * Create a query to return a list of paginated and sorted Listings that match the search
   * criteria.
   *
   * @param currentSession The Session
   * @param business       The business to query for the listings
   * @param pagBuilder     The pagination builder that holds all the clients pagination values
   * @return A Query that returns a list of paginated and sorted Listings
   */
  public static TypedQuery<Listing> listPaginatedAndSortedBusinessListings(
      Session currentSession, Business business, PaginationBuilder pagBuilder)
      throws InvalidPaginationInputException {
    // Setup
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();

    // Listings Query
    CriteriaQuery<Listing> criteriaQuery = criteriaBuilder.createQuery(Listing.class);
    Root<Listing> listing = criteriaQuery.from(Listing.class);
    Join<Listing, InventoryItem> inventoryItem = listing.join("inventoryItem");
    Join<InventoryItem, Product> product = inventoryItem.join("product");
    criteriaQuery.where(criteriaBuilder.equal(inventoryItem.get("business"), business));

    // Sorting query
    Path<Object> path;
    switch ((ListingSortByOption) pagBuilder.getSortField()) {
      case QUANTITY:
      case PRICE:
      case CREATED:
      case CLOSES:
        path = listing.get(pagBuilder.getSortField().toString());
        break;
      case NAME:
        path = product.get(pagBuilder.getSortField().toString());
        break;
      default:
        throw new IllegalStateException(
            "Unexpected value: " + pagBuilder.getSortField());
    }
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
}