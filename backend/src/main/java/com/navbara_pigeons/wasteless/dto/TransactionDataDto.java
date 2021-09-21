package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.model.TransactionReportModel;
import java.util.List;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import lombok.Data;

/**
 * A DTO containing transaction information for reporting over multiple transactions
 */
@Data
public class TransactionDataDto {

  private List<TransactionReportModel> transactions;

  @DecimalMin(message = "Total sales cannot be less than 0", value = "0")
  private Integer totalAmount;

  @DecimalMin(message = "Total number of orders cannot be less than 0", value = "0")
  private Double totalTransactionCount;

  public TransactionDataDto(List<TransactionReportModel> transactions, Integer totalAmount,
      Double totalTransactionCount) {
    this.transactions = transactions;
    this.totalAmount = totalAmount;
    this.totalTransactionCount = totalTransactionCount;
  }

  public TransactionDataDto() {
  }
}
