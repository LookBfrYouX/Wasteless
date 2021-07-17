package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;
import javax.persistence.EntityManager;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Repository;

@Repository
public class MarketListingDaoHibernateImpl implements MarketListingDao {

  private final EntityManager entityManager;

  public MarketListingDaoHibernateImpl(@Autowired EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  public void saveMarketListing(MarketListing marketListing) {
    Session currentSession = getSession();
    currentSession.saveOrUpdate(marketListing);
  }

  @Override
  public Pair<List<MarketListing>, Long> getMarketListing(
      String section, PaginationBuilder pagBuilder) {
    Session currentSession = getSession();
    List<MarketListing> serverResults =
        HibernateCriteriaQueryBuilder.listPaginatedAndSortedMarketListings(
                currentSession, section, pagBuilder)
            .getResultList();
    Long totalCountOfSection =
        HibernateCriteriaQueryBuilder.createTotalMarketListingsCountQuery(currentSession, section)
            .getSingleResult();

    return Pair.of(serverResults, totalCountOfSection);
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
