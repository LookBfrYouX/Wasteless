package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Listing;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * A Data Access Object utilising Hibernate to connect to the database and run basic queries.
 */
@Repository
public class ListingDaoImpl implements ListingDao {

  private final EntityManager entityManager;

  /**
   * Constructor to initiate properties for the DAO class.
   *
   * @param entityManager Passed in entity used to interact with the persistence context.
   */
  public ListingDaoImpl(@Autowired EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * Save a listing to the Db
   *
   * @param listing The listing to be saved
   */
  @Override
  public void saveListing(Listing listing) {
    Session currentSession = getSession();
    currentSession.saveOrUpdate(listing);
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
