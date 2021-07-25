package com.navbara_pigeons.wasteless.dto;


import com.navbara_pigeons.wasteless.entity.InventoryItem;
import java.time.LocalDate;

import com.navbara_pigeons.wasteless.validation.constraints.NotTooDistantFuture;
import com.navbara_pigeons.wasteless.validation.constraints.NotTooDistantPast;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;

@Data
public class CreateInventoryItemDto {

  private long productId;
  private long quantity;
  private Double pricePerItem;
  private Double totalPrice;

  @PastOrPresent
  @NotTooDistantPast(minYears = "10", message = "Manufacture date too old")
  private LocalDate manufactured;


  private LocalDate sellBy;

  private LocalDate bestBefore;

  @FutureOrPresent
  @NotNull(message = "Expiry date required")
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
