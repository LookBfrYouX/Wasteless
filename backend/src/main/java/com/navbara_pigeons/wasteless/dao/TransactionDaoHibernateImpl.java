package com.navbara_pigeons.wasteless.dao;

import java.time.ZonedDateTime;
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
   */
  @Override
  public void getTransactionData(int businessId, ZonedDateTime startSaleDate,
      ZonedDateTime endSaleDate, String granularity) {

    Query query = entityManager.createQuery(
        "SELECT sum(t.amount) from Transaction t WHERE t.saleDate between :startDate and :endDate GROUP BY DAY(t.saleDate)")
        .setParameter("startDate", startSaleDate)
        .setParameter("endDate", endSaleDate);

    System.out.println(query.getResultList());

    // SELECT count(*) AS sales, sale_date AS date FROM 'transaction' WHERE 'sale_date' BETWEEN ${startSaleDate} AND ${endSaleDate} GROUP BY ${granularity}('sale_date');
  }
}
