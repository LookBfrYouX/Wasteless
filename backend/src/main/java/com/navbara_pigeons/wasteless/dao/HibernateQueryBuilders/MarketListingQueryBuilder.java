package com.navbara_pigeons.wasteless.dao.HibernateQueryBuilders;

import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class MarketListingQueryBuilder {

  private MarketListingQueryBuilder() {
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

  public static TypedQuery<MarketListing> listPaginatedAndSortedMarketListings(
      Session currentSession, String section, PaginationBuilder pagBuilder) {
    // Setup
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<MarketListing> criteriaQuery = criteriaBuilder.createQuery(MarketListing.class);

    Root<MarketListing> root = criteriaQuery.from(MarketListing.class);
    criteriaQuery.where(criteriaBuilder.equal(root.get("section"), section));

    // Sorting query
    Path<Object> path = root.get(pagBuilder.getSortByFieldName());
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