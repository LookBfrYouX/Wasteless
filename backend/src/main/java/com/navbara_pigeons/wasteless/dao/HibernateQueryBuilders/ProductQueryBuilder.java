package com.navbara_pigeons.wasteless.dao.HibernateQueryBuilders;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class ProductQueryBuilder {

  private ProductQueryBuilder() {
  }

  public static Query<Long> createTotalProductsCountQuery(
      Session currentSession, Business business) {
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    Root<Business> businessRoot = countQuery.from(Business.class);
    Join<Business, Product> productsCatalogue = businessRoot.join("productsCatalogue");
    countQuery.where(criteriaBuilder.equal(businessRoot.get("id"), business.getId()));
    countQuery.select(criteriaBuilder.count(productsCatalogue));

    return currentSession.createQuery(countQuery);
  }

  public static TypedQuery<Product> listPaginatedAndSortedBusinessProducts(
      Session currentSession, Business business, PaginationBuilder pagBuilder) {
    // Setup
    // Tried and failed to use the criteria API to join businesses and products through catalogue
    // table, settled for using HQL
    String HQL =
        "SELECT bpc FROM Business b JOIN b.productsCatalogue bpc WHERE b.id=:business_id ORDER BY bpc."
            + pagBuilder.getSortByFieldName();
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
}