package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.dao.HibernateQueryBuilders.InventoryQueryBuilder;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class InventoryDaoHibernateImpl implements InventoryDao {

  private final EntityManager entityManager;

  /**
   * Constructor to initiate properties for the DAO class.
   *
   * @param entityManager Passed in entity used to interact with the persistence context.
   */
  public InventoryDaoHibernateImpl(@Autowired EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * Gets a list of the inventory items for a specific business. Also returns results in a paginated
   * form which is configured from the Pagination Builder.
   *
   * @param business   The specific business to get the information from
   * @param pagBuilder The Pagination Builder that holds this configurations for sorting and
   *                   paginating items
   * @return A paginated and sorted list of Inventory items and the total count of the entity (used
   * for client side pagination)
   */
  @Override
  public Pair<List<InventoryItem>, Long> getInventoryItems(Business business,
      PaginationBuilder pagBuilder) {
    Session currentSession = getSession();
    TypedQuery<InventoryItem> query =
        InventoryQueryBuilder.listPaginatedAndSortedBusinessInventory(
            currentSession, business, pagBuilder);
    Long totalCount =
        InventoryQueryBuilder.createTotalInventoryCountQuery(currentSession, business)
            .getSingleResult();
    return Pair.of(query.getResultList(), totalCount);
  }

  /**
   * This method saves a given inventoryItem to the database.
   *
   * @param inventory The inventoryItem to be saved or updated.
   */
  @Override
  public void saveInventoryItem(InventoryItem inventory) {
    Session currentSession = getSession();
    currentSession.saveOrUpdate(inventory);
  }


  /**
   * Deletes the given inventory item
   * @param inventoryItem inventory item to delete
   */
  @Override
  public void deleteInventoryItem(InventoryItem inventoryItem) {
    Session currentSession = getSession();
    currentSession.delete(inventoryItem);
  }

  /**
   * Get the entity manager session
   *
   * @return Instance of the Session class
   */
  private Session getSession() {
    return this.entityManager.unwrap(Session.class);
  }
}
