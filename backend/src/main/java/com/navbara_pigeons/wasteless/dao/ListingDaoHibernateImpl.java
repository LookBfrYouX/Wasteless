package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class ListingDaoHibernateImpl implements ListingDao {

  private final EntityManager entityManager;

  /**
   * Constructor to initiate properties for the DAO class.
   *
   * @param entityManager Passed in entity used to interact with the persistence context.
   */
  public ListingDaoHibernateImpl(@Autowired EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public List<Listing> getListings(Business business, PaginationBuilder pagBuilder) {
    Session currentSession = getSession();
    TypedQuery<Listing> query = HibernateCriteriaQueryBuilder
        .listPaginatedAndSortedBusinessListings(currentSession, entityManager, business,
            pagBuilder);
    return query.getResultList();
  }

  /**
   * This method saves a given inventoryItem to the database.
   *
   * @param listing The listing item to be saved or updated.
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
