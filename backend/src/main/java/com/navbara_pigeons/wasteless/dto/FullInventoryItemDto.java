package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.InventoryItem;
import java.time.LocalDate;
import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class FullInventoryItemDto {

  private long id;
  private BasicProductDto product;

  @NotNull(message = "Quantity Cannot Be Null")
  private long quantity;
  //Could be calculated value
  private Double pricePerItem;

  //Could be calculated value
  private Double totalPrice;

  @NotNull(message = "Expiry Date Cannot Be Null")
  private LocalDate expires;
  private LocalDate manufactured;
  private LocalDate sellBy;
  private LocalDate bestBefore;

  public FullInventoryItemDto(InventoryItem inventoryItem, String publicPathPrefix) {
    id = inventoryItem.getId();
    product = new BasicProductDto(inventoryItem.getProduct(), publicPathPrefix);
    quantity = inventoryItem.getQuantity();
    pricePerItem = inventoryItem.getPricePerItem();
    totalPrice = inventoryItem.getTotalPrice();
    expires = inventoryItem.getExpires();
    manufactured = inventoryItem.getManufactured();
    sellBy = inventoryItem.getSellBy();
    bestBefore = inventoryItem.getBestBefore();
  }

  public FullInventoryItemDto() {

  }

  ;
}
