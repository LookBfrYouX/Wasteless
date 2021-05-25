package com.navbara_pigeons.wasteless.dto;


import com.navbara_pigeons.wasteless.entity.InventoryItem;
import java.time.LocalDate;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

@Data
public class BasicInventoryItemDto {

  private long id;
  private BasicProductDto product;
  private long quantity;
  private Double pricePerItem;
  private Double totalPrice;
  private LocalDate manufactured;
  private LocalDate sellBy;
  private LocalDate bestBefore;
  private LocalDate expires;

  public BasicInventoryItemDto(InventoryItem inventoryItem) {
    this.id = inventoryItem.getId();
    this.product = new BasicProductDto(inventoryItem.getProduct());
    this.quantity = inventoryItem.getQuantity();
    this.pricePerItem = inventoryItem.getPricePerItem();
    this.totalPrice = inventoryItem.getTotalPrice();
    this.manufactured = inventoryItem.getManufactured();
    this.sellBy = inventoryItem.getSellBy();
    this.bestBefore = inventoryItem.getBestBefore();
    this.expires = inventoryItem.getExpires();
  }
}
