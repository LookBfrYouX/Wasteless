package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.Transaction;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class TransactionDto {

  private Long transactionId;
  private Long businessId;
  private Product product;
  private Double amount;
  private ZonedDateTime listingDate;
  private ZonedDateTime saleDate;

  /**
   * Constructor to create Transaction DTO
   *
   * @param transaction the transaction
   */
  public TransactionDto(Transaction transaction) {
    this.transactionId = transaction.getId();
    this.saleDate = transaction.getSaleDate();
    this.listingDate = transaction.getListingDate();
    this.businessId = transaction.getBusinessId();
    this.product = transaction.getProduct();
    this.amount = transaction.getAmount();
  }

  public TransactionDto() {

  }

}
