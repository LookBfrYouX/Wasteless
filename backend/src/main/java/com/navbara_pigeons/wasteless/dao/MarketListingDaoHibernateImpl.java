package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.MarketListing;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaQuery;
import org.hibernate.Session;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
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
  public List<MarketListing> getMarketListing(String section) {
    Session currentSession = getSession();
    CriteriaQuery<MarketListing> criteriaQuery = HibernateCriteriaQueryBuilder
        .parseListingQuery(currentSession, section);

    Query<MarketListing> query = currentSession.createQuery(criteriaQuery);
    List<MarketListing> results = query.getResultList();

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
