package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * A Data Access Object utilising Hibernate to connect to the database and run basic queries to
 * retrieve products.
 */
@Repository
public class ProductDaoHibernateImpl implements ProductDao {

  private final EntityManager entityManager;

  /**
   * Constructor to initiate properties for the DAO class.
   *
   * @param entityManager Passed in entity used to interact with the persistence context.
   */
  public ProductDaoHibernateImpl(@Autowired EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * This method saves a given product to the database.
   *
   * @param product The product to be saved or updated.
   */
  @Override
  public void saveProduct(Product product) {
    Session currentSession = getSession();
    currentSession.saveOrUpdate(product);
  }

  /**
   * This method returns a product from the database.
   *
   * @param productId The id of the product to be retreived.
   * @return Product to be returned
   * @throws ProductNotFoundException when no product is found
   */
  @Override
  public Product getProduct(long productId) throws ProductNotFoundException {
    Session currentSession = getSession();
    Product product = currentSession.get(Product.class, productId);
    if (product == null) {
      throw new ProductNotFoundException(productId);
    }
    return product;
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
