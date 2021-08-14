package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.dao.HibernateQueryBuilders.ListingQueryBuilder;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
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

  /**
   * Get a list of Listings for a specific Business.
   *
   * @param business   The specific business to get the information from
   * @param pagBuilder The Pagination Builder that holds this configurations for sorting and
   *                   paginating items
   * @return A paginated and sorted list of Listings and the total count of the entity (used for
   * client side pagination)
   */
  @Override
  public Pair<List<Listing>, Long> getListings(Business business, PaginationBuilder pagBuilder)
      throws InvalidPaginationInputException {
    Session currentSession = getSession();

    List<Listing> serverResult =
        ListingQueryBuilder.listPaginatedAndSortedBusinessListings(
            currentSession, business, pagBuilder).getResultList();
    Long totalCount =
        ListingQueryBuilder.createTotalListingsCountQuery(currentSession, business)
            .getSingleResult();

    return Pair.of(serverResult, totalCount);
  }

  /**
   * This method saves a given listing to the database.
   *
   * @param listing The listing item to be saved or updated.
   */
  @Override
  public void saveListing(Listing listing) {
    Session currentSession = getSession();
    currentSession.saveOrUpdate(listing);
  }

  
  /**
   * This method deletes a given listing to the database.
   *
   * @param listingId of the the listing to be deleted.
   */
  @Override
  public void deleteListing(Long listingId) {
    Session currentSession = getSession();
    Listing listing = currentSession.load(Listing.class, listingId);
    currentSession.delete(listing);
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
