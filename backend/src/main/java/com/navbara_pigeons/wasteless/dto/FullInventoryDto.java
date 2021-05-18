package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Inventory;
import lombok.Data;

import java.time.LocalDate;

@Data
public class FullInventoryDto {
  private long id;
  private BasicProductDto product;
  private long quantity;
  private Double price;
  private Double totalPrice;
  private LocalDate expires;
  private LocalDate manufactured;
  private LocalDate sellBy;
  private LocalDate bestBefore;

  public FullInventoryDto(Inventory inventory, String publicPathPrefix) {
    id = inventory.getId();
    product = new BasicProductDto(inventory.getProduct(), publicPathPrefix);
    quantity = inventory.getQuantity();
    price = inventory.getPricePerItem();
    totalPrice = inventory.getTotalPrice();
    expires = inventory.getExpires();
    manufactured = inventory.getManufactured();
    sellBy = inventory.getSellBy();
    bestBefore = inventory.getBestBefore();
  }

  public FullInventoryDto() {

  };
}
