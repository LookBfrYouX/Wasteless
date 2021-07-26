package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.InventoryItem;
import java.time.LocalDate;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class FullInventoryItemDto {

  private long id;
  private BasicProductDto product;
  private long quantity;
  @DecimalMin(message="pricePerItem must be above 0.01", value="0.01")
  @DecimalMax(message="pricePerItem must be below 10,000,000", value="10000000.00")
  private Double pricePerItem;
  @DecimalMin(message="totalPrice must be above 0.01", value="0.01")
  @DecimalMax(message="totalPrice must be below 10,000,000", value="10000000.00")
  private Double totalPrice;
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

}
