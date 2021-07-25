package com.navbara_pigeons.wasteless.dto;


import com.navbara_pigeons.wasteless.entity.InventoryItem;
import java.time.LocalDate;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class CreateInventoryItemDto {

  private long productId;
  private long quantity;
  @DecimalMin(message="pricePerItem must be above 0.01", value="0.01")
  @DecimalMax(message="pricePerItem must be below 10,000,000", value="10000000.00")
  private Double pricePerItem;
  @DecimalMin(message="totalPrice must be above 0.01", value="0.01")
  @DecimalMax(message="totalPrice must be below 10,000,000", value="10000000.00")
  private Double totalPrice;
  private LocalDate manufactured;
  private LocalDate sellBy;
  private LocalDate bestBefore;
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
