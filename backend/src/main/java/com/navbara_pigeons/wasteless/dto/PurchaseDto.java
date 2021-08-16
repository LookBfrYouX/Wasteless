package com.navbara_pigeons.wasteless.dto;

import lombok.Data;

@Data
public class PurchaseDto {

  private Long transactionId;

  /**
   * Constructor to create Purchase DTO
   *
   * @param transactionId the transaction Id
   */
  public PurchaseDto(Long transactionId) {
    this.transactionId = transactionId;
  }

  public PurchaseDto() {

  }

}
