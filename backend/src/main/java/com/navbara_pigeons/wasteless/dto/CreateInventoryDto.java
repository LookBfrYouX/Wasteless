package com.navbara_pigeons.wasteless.dto;


import com.navbara_pigeons.wasteless.entity.Inventory;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateInventoryDto {
  private long productId;
  private long quantity;
  private float pricePerItem;
  private float totalPrice;
  private LocalDate manufactured;
  private LocalDate sellBy;
  private LocalDate bestBefore;
  private LocalDate expires;

  public CreateInventoryDto(Inventory inventoryItem) {
    this.productId = inventoryItem.getProduct().getId();
    this.quantity = inventoryItem.getQuantity();
    this.pricePerItem = inventoryItem.getPricePerItem();
    this.totalPrice = inventoryItem.getTotalPrice();
    this.manufactured = inventoryItem.getManufactured();
    this.sellBy = inventoryItem.getSellBy();
    this.bestBefore = inventoryItem.getBestBefore();
    this.expires = inventoryItem.getExpires();
  }

  public CreateInventoryDto() {};
}
