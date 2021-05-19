package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.InventoryItem;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FullInventoryItemDto {
  private long id;
  private BasicProductDto product;
  private long quantity;
  private Double price;
  private Double totalPrice;
  private LocalDate expires;
  private LocalDate manufactured;
  private LocalDate sellBy;
  private LocalDate bestBefore;

  public FullInventoryItemDto(InventoryItem inventoryItem) {
    id = inventoryItem.getId();
    product = new BasicProductDto(inventoryItem.getProduct());
    quantity = inventoryItem.getQuantity();
    price = inventoryItem.getPricePerItem();
    totalPrice = inventoryItem.getTotalPrice();
    expires = inventoryItem.getExpires();
    manufactured = inventoryItem.getManufactured();
    sellBy = inventoryItem.getSellBy();
    bestBefore = inventoryItem.getBestBefore();
  }

  public FullInventoryItemDto() {

  };
}
