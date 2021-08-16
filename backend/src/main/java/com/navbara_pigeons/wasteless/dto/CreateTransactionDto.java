package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Product;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class CreateTransactionDto {

  private ZonedDateTime saleDate;
  private ZonedDateTime listingDate;
  private Product product;
  private Double amount;


  public CreateTransactionDto(ZonedDateTime saleDate, ZonedDateTime listingDate, Product product,
      Double amount) {
    this.saleDate = saleDate;
    this.listingDate = listingDate;
    this.product = product;
    this.amount = amount;
  }

  public CreateTransactionDto() {

  }

}
