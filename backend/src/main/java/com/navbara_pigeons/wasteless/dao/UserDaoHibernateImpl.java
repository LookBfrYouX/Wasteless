package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.management.InvalidAttributeValueException;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * A Data Access Object utilising Hibernate to connect to the database and run basic queries to
 * retrieve users.
 */
@Repository
public class UserDaoHibernateImpl implements UserDao {

  private final EntityManager entityManager;

  /**
   * Constructor to initiate properties for the DAO class.
   *
   * @param entityManager Passed in entity used to interact with the persistence context.
   */
  public UserDaoHibernateImpl(@Autowired EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * Method to save a User entity to the database.
   *
   * @param user Passed in User model
   */
  @Override
  public void saveUser(User user) {
    Session currentSession = getSession();
    currentSession.saveOrUpdate(user);
  }

  /**
   * Method to remove a user from the database
   *
   * @param user Passed in User model
   */
  @Override
  public void deleteUser(User user) {
    Session currentSession = getSession();
    currentSession.delete(user);
  }

  /**
   * Method to retrieve a User by the corresponding id. Throws Runtime exception if no or many users
   * are found.
   *
   * @param id Id to match user
   * @return Found User model
   * @throws UserNotFoundException Thrown if too few/many users.
   */
  @Override
  public User getUserById(long id) throws UserNotFoundException {
    Session currentSession = getSession();
    User user = currentSession.get(User.class, id);
    if (user == null) {
      throw new UserNotFoundException("No user with ID: " + id);
    }
    return user;
  }

  /**
   * Method to retrieve a User by the corresponding Email address. Throws Runtime exception if no or
   * many users are found.
   *
   * @param email Email to match user
   * @return Found User model
   * @throws RuntimeException Thrown if too few/many users.
   */
  @Override
  public User getUserByEmail(String email) throws UserNotFoundException {
    Session currentSession = getSession();
    Query<User> query = currentSession.createQuery("FROM User WHERE email=:email", User.class);
    query.setParameter("email", email);
    List<User> user = new ArrayList<>();
    try {
      user = query.getResultList();
    } catch (Exception e) {
      throw new RuntimeException("Unable to retrieve data from database");
    }
    if (user.size() == 0) {
      throw new UserNotFoundException("No user found with email: " + email);
    } else if (user.size() > 1) {
      throw new RuntimeException("Multiple users found with email: " + email);
    }
    return user.get(0);
  }

  /**
   * Returns whether a user exists by their unique email.
   *
   * @param email The users unique email.
   * @return a Boolean value whether the user exists in the Db.
   */
  @Override
  public boolean userExists(String email) {
    try {
      getUserByEmail(email);
      return true;
    } catch (Exception exc) {
      return false;
    }
  }

  @Override
  public List<User> searchUsers(String searchQuery) throws InvalidAttributeValueException {
    Session currentSession = getSession();
    // CriteriaBuilder criteriaBuilder = currentSession.getCriteriaBuilder();
    CriteriaQuery<User> criteriaQuery = HibernateCriteriaQueryBuilder
        .parseUserSearchQuery(currentSession, searchQuery);

    Query<User> query = currentSession.createQuery(criteriaQuery);
    List<User> results = query.getResultList();

    return results;
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
