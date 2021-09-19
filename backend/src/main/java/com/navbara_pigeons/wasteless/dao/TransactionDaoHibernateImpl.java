package com.navbara_pigeons.wasteless.dao;

import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionDaoHibernateImpl implements TransactionDaoHibernate {

  private final EntityManager entityManager;

  public TransactionDaoHibernateImpl(@Autowired EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  /**
   * Queries all transactions from business with given id in date range and groups by granularity
   *
   * @param businessId    Business to group by
   * @param startSaleDate Earliest date to query by
   * @param endSaleDate   Latest date to query by
   * @param granularity   Granularity to group by
   * @return a result list of transaction data
   */
  @Override
  public List getTransactionData(Long businessId, ZonedDateTime startSaleDate,
      ZonedDateTime endSaleDate, String granularity) {

    // This looks hacky but there is no other way to go about it (I even asked Max!)
    String grouping = "t.saleDate";
    if (granularity.equals("MONTH")) {
      grouping = "YEAR(t.saleDate), MONTH(t.saleDate)";
    } else if (granularity.equals("YEAR")) {
      grouping = "YEAR(t.saleDate)";
    }

    Query query = entityManager.createQuery(
            "SELECT t.saleDate, count(t) from Transaction t WHERE t.businessId = :businessId and t.saleDate between :startDate and :endDate  GROUP BY "
                + grouping)
        .setParameter("startDate", startSaleDate)
        .setParameter("endDate", endSaleDate)
        .setParameter("businessId", businessId);

    return query.getResultList();
  }
}
