package com.navbara_pigeons.wasteless.dto;


import com.navbara_pigeons.wasteless.entity.InventoryItem;
import java.time.LocalDate;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import com.navbara_pigeons.wasteless.validation.constraints.NotTooDistantFuture;
import com.navbara_pigeons.wasteless.validation.constraints.NotTooDistantPast;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

@Data
public class CreateInventoryItemDto {

  private long productId;

  @NotNull(message = "Quantity Cannot Be Null")
  @DecimalMin(inclusive = false, value = "0")
  private long quantity;
  @DecimalMin(message="pricePerItem must be above 0.01", value="0.01")
  @DecimalMax(message="pricePerItem must be below 10,000,000", value="10000000.00")
  private Double pricePerItem;
  @DecimalMin(message="totalPrice must be above 0.01", value="0.01")
  @DecimalMax(message="totalPrice must be below 10,000,000", value="10000000.00")
  private Double totalPrice;

  @PastOrPresent
  @NotTooDistantPast(years=10, message="Manufacture date too old")
  private LocalDate manufactured;
  private LocalDate sellBy;
  private LocalDate bestBefore;

  @NotNull(message = "Expiry Date Cannot Be Null")
  @FutureOrPresent
  @NotTooDistantFuture(message = "Expiry date too far into the future")
  private LocalDate expires;

  public CreateInventoryItemDto(InventoryItem inventoryItem) {
    this.productId = inventoryItem.getProduct().getId();
    this.quantity = inventoryItem.getQuantity();
    this.pricePerItem = inventoryItem.getPricePerItem();
    this.totalPrice = inventoryItem.getTotalPrice();
    this.manufactured = inventoryItem.getManufactured();
    this.sellBy = inventoryItem.getSellBy();
    this.bestBefore = inventoryItem.getBestBefore();
    this.expires = inventoryItem.getExpires();
  }

  public CreateInventoryItemDto() {
  }

  ;
}
