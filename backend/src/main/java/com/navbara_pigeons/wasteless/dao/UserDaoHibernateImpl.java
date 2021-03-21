package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * A Data Access Object utilising Hibernate to connect to the database and run basic queries.
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
      throw new UserNotFoundException("No user with ID: " + Float.toString(id));
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
      e.printStackTrace();
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

  /**
   * Method to retrieve a User(s) by the corresponding exact user names.
   *
   * @param searchQuery Exact query to match users
   * @return A list of Users that exactly match the passed username
   */
  public List<User> exactSearchUsers(String searchQuery) {
    Session currentSession = getSession();
    searchQuery = searchQuery.replaceAll("\\s", "");
    String queryString = "FROM User u WHERE LOWER(:searchQuery) IN ("
        + "LOWER(u.firstName), "
        + "LOWER(u.middleName), "
        + "LOWER(u.lastName), "
        + "LOWER(u.nickname), "
        + "CONCAT(LOWER(u.firstName), LOWER(u.middleName), LOWER(u.lastName)), "
        + "CONCAT(LOWER(u.firstName), LOWER(u.lastName))"
        + ")";
    Query<User> query = currentSession
        .createQuery(queryString, User.class);
    query.setParameter("searchQuery", searchQuery);
    List<User> users = new ArrayList<>();
    try {
      users = query.getResultList();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return users;
  }

  /**
   * Method to retrieve a User(s) by the a partial search (LIKE).
   *
   * @param searchQuery Query to match users
   * @return A list of Users that partially match the passed username
   */
  public List<User> partialSearchUsers(String searchQuery) {
    Session currentSession = getSession();
    String queryString = "FROM User u WHERE LOWER(u.nickname) LIKE LOWER(:searchQuery) "
        + "OR LOWER(u.firstName) LIKE LOWER(:searchQuery) "
        + "OR LOWER(u.middleName) LIKE LOWER(:searchQuery) "
        + "OR LOWER(u.lastName) LIKE LOWER(:searchQuery)";
    Query<User> query = currentSession
        .createQuery(queryString, User.class);
    query.setParameter("searchQuery", "%" + searchQuery + "%");
    List<User> users = new ArrayList<>();
    try {
      users = query.getResultList();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return users;
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
