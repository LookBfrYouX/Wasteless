package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.dto.TransactionDataDto;
import com.navbara_pigeons.wasteless.enums.TransactionGranularity;
import com.navbara_pigeons.wasteless.model.TransactionReportModel;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
  public TransactionDataDto getTransactionData(Long businessId, ZonedDateTime startSaleDate,
      ZonedDateTime endSaleDate, TransactionGranularity granularity) {

    String grouping = "t.saleDate";
    if (granularity == TransactionGranularity.WEEK) {
      grouping = "YEAR(t.saleDate), MONTH(t.saleDate), WEEK(t.saleDate)";
    } else if (granularity == TransactionGranularity.MONTH) {
      grouping = "YEAR(t.saleDate), MONTH(t.saleDate)";
    } else if (granularity == TransactionGranularity.YEAR) {
      grouping = "YEAR(t.saleDate)";
    }

    Query transactionQueries = entityManager.createQuery(
            "SELECT t.saleDate, count(t), sum(t.amount) from Transaction t WHERE t.businessId = :businessId and t.saleDate between :startDate and :endDate GROUP BY "
                + grouping)
        .setParameter("startDate", startSaleDate)
        .setParameter("endDate", endSaleDate)
        .setParameter("businessId", businessId);

    Query totalsQuery = entityManager.createQuery(
            "SELECT count(t), sum(t.amount) from Transaction t WHERE t.businessId = :businessId and t.saleDate between :startDate and :endDate")
        .setParameter("startDate", startSaleDate)
        .setParameter("endDate", endSaleDate)
        .setParameter("businessId", businessId);

    // Elements 0, 1, 2 and 3 have date, transactionCount and amount in them respectively
    List<Object[]> results = transactionQueries.getResultList();
    // Elements 0 and 1 have totalTransactionCount and amount in them respectively
    List<Object[]> totals = totalsQuery.getResultList();

    // If no transactions were found, return empty TransactionDataDto
    if (results.isEmpty() || totals.isEmpty()) {
      return new TransactionDataDto(new ArrayList<>(), 0, 0.00);
    }
    List<TransactionReportModel> transactionReportModels = new ArrayList<>();
    for (Object[] result : results) {
      ZonedDateTime date = ZonedDateTime.parse(result[0].toString());
      Integer transactionCount = Integer.parseInt(result[1].toString());
      Double amount = Double.valueOf(result[2].toString());

      TransactionReportModel transactionReportModel = new TransactionReportModel(date,
          transactionCount, amount);
      transactionReportModels.add(transactionReportModel);
    }
    Integer totalTransactionCount = Integer.parseInt(totals.get(0)[0].toString());
    Double totalAmount = Double.valueOf(totals.get(0)[1].toString());

    return new TransactionDataDto(transactionReportModels, totalTransactionCount, totalAmount);
  }
}
