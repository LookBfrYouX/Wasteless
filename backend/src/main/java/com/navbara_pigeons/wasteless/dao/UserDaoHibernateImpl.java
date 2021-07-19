package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.dao.HibernateQueryBuilders.UserQueryBuilder;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.enums.ProductSortByOption;
import com.navbara_pigeons.wasteless.enums.SortByOption;
import com.navbara_pigeons.wasteless.enums.UserSortByOption;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.ArrayList;
import java.util.List;
import javax.management.InvalidAttributeValueException;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
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
  public List<User> searchUsers(String searchQuery)
      throws InvalidAttributeValueException, InvalidPaginationInputException {
    String defaultSortField = InventoryItem.class.getDeclaredFields()[0].getName();
    SortByOption thing = UserSortByOption.FIRST_NAME;
    ProductSortByOption.valueOf("");
    PaginationBuilder pagBuilder = new PaginationBuilder(User.class, thing);
    return searchUsers(searchQuery, pagBuilder).getFirst();
  }

  /**
   * Search for a list of users.
   *
   * @param searchQuery Search query ( can include AND, OR's )
   * @param pagBuilder  The Pagination Builder that holds this configurations for sorting and
   *                    paginating items
   * @return A paginated and sorted list of Users and the total count of the entity (used for client
   * side pagination)
   */
  @Override
  public Pair<List<User>, Long> searchUsers(String searchQuery, PaginationBuilder pagBuilder)
      throws InvalidAttributeValueException, InvalidPaginationInputException {
    Session currentSession = getSession();
    List<User> serverResults = UserQueryBuilder
        .listPaginatedAndSortedUsers(currentSession, searchQuery, pagBuilder).getResultList();
    Long totalCountOfQuery = entityManager.createQuery(
        UserQueryBuilder.createTotalUserCountQuery(currentSession, searchQuery))
        .getSingleResult();

    return Pair.of(serverResults, totalCountOfQuery);
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
