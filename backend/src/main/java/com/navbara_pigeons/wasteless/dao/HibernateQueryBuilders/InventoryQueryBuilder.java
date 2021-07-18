package com.navbara_pigeons.wasteless.dao.HibernateQueryBuilders;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Root;
import org.hibernate.Session;
import org.hibernate.query.Query;

public class InventoryQueryBuilder {

  private InventoryQueryBuilder() {
  }

  public static Query<Long> createTotalInventoryCountQuery(
      Session currentSession, Business business) {
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    Root<InventoryItem> inventoryItemRoot = countQuery.from(InventoryItem.class);
    countQuery.where(criteriaBuilder.equal(inventoryItemRoot.get("business"), business));
    countQuery.select(criteriaBuilder.count(inventoryItemRoot));

    return currentSession.createQuery(countQuery);
  }

  public static TypedQuery<InventoryItem> listPaginatedAndSortedBusinessInventory(
      Session currentSession, Business business, PaginationBuilder pagBuilder) {
    // Setup
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<InventoryItem> criteriaQuery = criteriaBuilder.createQuery(InventoryItem.class);

    Root<InventoryItem> root = criteriaQuery.from(InventoryItem.class);

    criteriaQuery.where(criteriaBuilder.equal(root.get("business"), business));

    // Sorting query
    Path<Object> path = root.get(pagBuilder.getSortByFieldName());
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
}