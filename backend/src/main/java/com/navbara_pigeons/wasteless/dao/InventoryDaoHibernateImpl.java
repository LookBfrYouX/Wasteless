package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Inventory;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * A Data Access Object utilising Hibernate to connect to the database and run basic queries.
 */
@Repository
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
   * This method returns an inventory item from the database.
   *
   * @param inventoryId The id of the inventory item to be retrieved.
   * @return InventoryItem to be returned
   */
  @Override
  public Inventory getInventoryItem(long inventoryId) throws InventoryItemNotFoundException {
    Session currentSession = getSession();
    Inventory inventory = currentSession.get(Inventory.class, inventoryId);
    if (inventory == null) {
      throw new InventoryItemNotFoundException(inventoryId);
    }
    return inventory;
  }

  public List<Inventory> getBusinessesInventory(long businessId) {
    System.out.println("test4");
    Session currentSession = getSession();
    Query query = currentSession.createQuery("SELECT * FROM INVENTORY WHERE BUSINESS_ID = :businessId");
    query.setParameter("businessId", businessId);
    List<Inventory> inventory = query.getResultList();
    System.out.println(inventory);
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
