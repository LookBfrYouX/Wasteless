package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Inventory;
import com.navbara_pigeons.wasteless.entity.Product;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class BasicInventoryDto {

  private long id;
  private BasicProductDto product;
  private long quantity;
  private float pricePerItem;
  private float totalPrice;
  private ZonedDateTime manufactured;
  private ZonedDateTime sellBy;
  private ZonedDateTime bestBefore;
  private ZonedDateTime expires;

  public BasicInventoryDto(Inventory inventoryItem, String publicPathPrefix) {
    this.id = inventoryItem.getId();
    this.product = new BasicProductDto(inventoryItem.getProduct(), publicPathPrefix);
    this.quantity = inventoryItem.getQuantity();
    this.pricePerItem = inventoryItem.getPricePerItem();
    this.totalPrice = inventoryItem.getTotalPrice();
    this.manufactured = inventoryItem.getManufactured();
    this.sellBy = inventoryItem.getSellBy();
    this.bestBefore = inventoryItem.getBestBefore();
    this.expires = inventoryItem.getExpires();
  }
}
