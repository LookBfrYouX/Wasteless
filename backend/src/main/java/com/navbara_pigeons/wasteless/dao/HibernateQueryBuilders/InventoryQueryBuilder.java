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

  /**
   * Create a query for retrieving the total count of the clients query
   *
   * @param currentSession The Session
   * @param business       The business to query for the inventory items
   * @return A Query for retrieving the total count (Long) of the clients query
   */
  public static Query<Long> createTotalInventoryCountQuery(
      Session currentSession, Business business) {
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<Long> countQuery = criteriaBuilder.createQuery(Long.class);
    Root<InventoryItem> inventoryItemRoot = countQuery.from(InventoryItem.class);
    countQuery.where(criteriaBuilder.equal(inventoryItemRoot.get("business"), business));
    countQuery.select(criteriaBuilder.count(inventoryItemRoot));

    return currentSession.createQuery(countQuery);
  }

  /**
   * Create a query to return a list of paginated and sorted Inventory Items that match the search
   * criteria.
   *
   * @param currentSession The Session
   * @param business       The business to query for the inventory items
   * @param pagBuilder     The pagination builder that holds all the clients pagination values
   * @return A Query that returns a list of paginated and sorted Inventory Items
   */
  public static TypedQuery<InventoryItem> listPaginatedAndSortedBusinessInventory(
      Session currentSession, Business business, PaginationBuilder pagBuilder) {
    // Setup
    CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<InventoryItem> criteriaQuery = criteriaBuilder.createQuery(InventoryItem.class);

    Root<InventoryItem> root = criteriaQuery.from(InventoryItem.class);

    criteriaQuery.where(criteriaBuilder.equal(root.get("business"), business));

    // Sorting query
    Path<Object> path = root.get(pagBuilder.getSortField().toString());
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