package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.model.TransactionReportModel;
import java.util.List;
import lombok.Data;

/**
 * A DTO containing transaction information for reporting over multiple transactions
 */
@Data
public class TransactionDataDto {

  private List<TransactionReportModel> transactions;
  private Integer totalAmount;
  private Double totalTransactionCount;

  public TransactionDataDto(List<TransactionReportModel> transactions, Integer totalAmount,
      Double totalTransactionCount) {
    this.transactions = transactions;
    this.totalAmount = totalAmount;
    this.totalTransactionCount = totalTransactionCount;
  }
}
