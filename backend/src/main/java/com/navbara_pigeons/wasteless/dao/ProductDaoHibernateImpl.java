package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Product;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

/**
 * A Data Access Object utilising Hibernate to connect to the database and run basic queries to retrieve products.
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
     * @param product The product to be saved or updated.
     */
    @Override
    public void saveProduct(Product product) {
        Session currentSession = getSession();
    }

    /**
     * This method retrieves all products listed by one business
     * @param businessId The id of the business.
     * @return products A List<Product> of products.
     */
    @Override
    public List<Product> getProducts(int businessId) {
        return null;
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
