package com.navbara_pigeons.wasteless.dto;


import com.navbara_pigeons.wasteless.entity.InventoryItem;
import java.time.LocalDate;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class CreateInventoryItemDto {

  private long productId;

  @NotNull(message = "Quantity Cannot Be Null")
  private long quantity;
  private Double pricePerItem;
  private Double totalPrice;
  private LocalDate manufactured;
  private LocalDate sellBy;
  private LocalDate bestBefore;

  @NotNull(message = "Expiry Date Cannot Be Null")
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
