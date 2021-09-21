package com.navbara_pigeons.wasteless.model;

import java.time.ZonedDateTime;
import lombok.Data;

/**
 * A model to hold data for multiple transactions
 */
@Data
public class TransactionReportModel {

  private ZonedDateTime date;
  private Integer transactionCount;
  private Double amount;

  public TransactionReportModel(ZonedDateTime date, Integer transactionCount, Double amount) {
    this.date = date;
    this.transactionCount = transactionCount;
    this.amount = amount;
  }
}
