package com.navbara_pigeons.wasteless.dao.HibernateQueryBuilders;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
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
    Path<Object> path = listing.get(pagBuilder.getSortByFieldName());
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