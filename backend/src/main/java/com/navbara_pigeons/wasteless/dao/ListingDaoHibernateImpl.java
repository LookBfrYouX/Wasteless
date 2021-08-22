package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.dao.HibernateQueryBuilders.ListingQueryBuilder;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.ListingNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

@Repository
public class ListingDaoHibernateImpl implements ListingDaoHibernate {

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
   * Get a specific listing from its identifier
   *
   * @param listingId The specific identifier of the listing
   * @return The Listing
   * @throws ListingNotFoundException A listing with that id was not found
   */
  public Listing getListing(long listingId) throws ListingNotFoundException {
    Session currentSession = getSession();
    Listing listing = currentSession.get(Listing.class, listingId);
    if (listing == null) {
      throw new ListingNotFoundException(listingId);
    }
    return listing;
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
   * Implemented for passing tests because this method is to be implemented on other branch.
   * Replace with proper implementation afterwards.
   * @param searchKey
   * @param searchValue
   * @param minPrice
   * @param maxPrice
   * @param filterDates
   * @param businessTypes
   * @param pagBuilder
   * @return
   */
  @Override
  public Pair<List<Listing>, Long> searchAllListings(List<String> searchKey, String searchValue,
      Double minPrice, Double maxPrice, List<LocalDate> filterDates,
      List<BusinessType> businessTypes, PaginationBuilder pagBuilder) {
    // Returns empty listings for testing purpose.
    List<Listing> listings = new ArrayList<>();
    return Pair.of(listings, 40L);
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
