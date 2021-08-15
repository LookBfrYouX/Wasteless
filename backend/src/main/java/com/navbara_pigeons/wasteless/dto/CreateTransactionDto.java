package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.entity.Transaction;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class CreateTransactionDto {

  private ZonedDateTime saleDate;
  private ZonedDateTime listingDate;
  private Product product;
  private Double amount;


  public CreateTransactionDto(Transaction transaction) {
    this.saleDate = transaction.getSaleDate();
    this.listingDate = transaction.getListingDate();
    this.product = transaction.getProduct();
    this.amount = transaction.getAmount();
  }

  public CreateTransactionDto() {

  }

}
