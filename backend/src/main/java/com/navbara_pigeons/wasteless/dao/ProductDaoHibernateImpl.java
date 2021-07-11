package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.ProductNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
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
   * Gets a list of the products for a specific business. Also returns results in a paginated
   * form which is configured from the Pagination Builder.
   *
   * @param business The specific business to get the information from
   * @param pagBuilder The Pagination Builder that holds this configurations for sorting and paginating items
   * @return A paginated and sorted list of Products
   */
  @Override
  public List<Product> getProducts(Business business, PaginationBuilder pagBuilder) {
    Session currentSession = getSession();
    TypedQuery<Product> query = HibernateCriteriaQueryBuilder
        .listPaginatedAndSortedBusinessProducts(currentSession, business, pagBuilder);
    return query.getResultList();
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
