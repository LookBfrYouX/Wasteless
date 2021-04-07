package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Address;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class AddressDaoImpl implements AddressDao {

  private final EntityManager entityManager;

  public AddressDaoImpl(@Autowired EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * Method to save an Address entity to the database.
   *
   * @param address Passed in Address model
   */
  @Override
  public void saveAddress(Address address) {
    Session currentSession = getSession();
    currentSession.saveOrUpdate(address);
  }

  /**
   * Method to delete an Address entity from the database.
   *
   * @param address Passed in Address model
   */
  @Override
  public void deleteAddress(Address address) {
    Session currentSession = getSession();
    currentSession.delete(address);
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
