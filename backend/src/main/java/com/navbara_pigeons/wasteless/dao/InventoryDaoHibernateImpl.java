package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
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

  @Override
  public List<InventoryItem> getInventoryItems(Business business, PaginationBuilder pagBuilder) {
    Session currentSession = getSession();
    TypedQuery<InventoryItem> query = HibernateCriteriaQueryBuilder
        .listPaginatedAndSortedBusinessInventory(currentSession, business, pagBuilder);
    return query.getResultList();
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
   * This method returns a product from the database.
   *
   * @param inventoryId The id of the inventoryItem to be retreived.
   * @return inventoryItem to be returned
   * @throws InventoryItemNotFoundException when no inventoryItem is found
   */
  @Override
  public InventoryItem getInventoryItem(long inventoryId) throws InventoryItemNotFoundException {
    Session currentSession = getSession();
    InventoryItem inventory = currentSession.get(InventoryItem.class, inventoryId);
    if (inventory == null) {
      throw new InventoryItemNotFoundException(inventoryId);
    }
    return inventory;
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
