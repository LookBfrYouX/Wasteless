package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Transaction;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * A Data Access Object utilising Hibernate to connect to the database and run basic queries to save
 * transactions.
 */
@Repository
public class TransactionDaoImpl implements TransactionDao {

  private final EntityManager entityManager;

  /**
   * Constructor to initiate properties for the DAO class.
   *
   * @param entityManager Passed in entity used to interact with the persistence context.
   */
  public TransactionDaoImpl(@Autowired EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * Method to save a Transaction entity to the database.
   *
   * @param transaction Passed in Transaction model
   */
  @Override
  public void saveTransaction(Transaction transaction) {
    Session currentSession = getSession();
    currentSession.saveOrUpdate(transaction);
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
