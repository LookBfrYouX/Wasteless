package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * A Data Access Object utilising Hibernate to connect to the database and run basic queries.
 */
@Repository
public class BusinessDaoHibernateImpl implements BusinessDao {

  private final EntityManager entityManager;

  /**
   * Constructor to initiate properties for the DAO class.
   *
   * @param entityManager Passed in entity used to interact with the persistence context.
   */
  public BusinessDaoHibernateImpl(@Autowired EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * Method to save a Business entity to the database.
   *
   * @param business Passed in Business model
   */
  @Override
  public void saveBusiness(Business business) {
    Session currentSession = getSession();
    currentSession.saveOrUpdate(business);
  }

  /**
   * Method to retrieve a Business by the corresponding id. Throws Runtime exception if no or many
   * businesses are found.
   *
   * @param id Id to match business
   * @return Found Business model
   * @throws BusinessNotFoundException Thrown if too few/many businesses.
   */
  @Override
  public Business getBusinessById(long id) throws BusinessNotFoundException {
    Session currentSession = getSession();
    Business business = currentSession.get(Business.class, id);
    if (business == null) {
      throw new BusinessNotFoundException("No business with ID: " + Float.toString(id));
    }
    return business;
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
