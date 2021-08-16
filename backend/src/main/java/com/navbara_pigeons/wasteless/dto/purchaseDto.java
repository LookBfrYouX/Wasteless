package com.navbara_pigeons.wasteless.dto;

import lombok.Data;

@Data
public class purchaseDto {

  private Long transactionId;

  /**
   * Constructor to create Purchase DTO
   *
   * @param transactionId the transaction Id
   */
  public purchaseDto(Long transactionId) {
    this.transactionId = transactionId;
  }

  public purchaseDto() {

  }

}
