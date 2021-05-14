package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Product;
import java.time.ZonedDateTime;

public class BasicInventoryDto {

  private long id;
  private Product product;
  private long quantity;
  private Double pricePerItem;
  private Double totalPrice;
  private ZonedDateTime manufatured;
  private ZonedDateTime sellBy;
  private ZonedDateTime bestBefore;
  private ZonedDateTime expires;

  public BasicInventoryDto(InventoryItem inventoryItem, String publicPathPrefix) {
    this.id = inventoryItem.getId();
    this.product = new BasicProductDto(inventoryItem.getProduct(), publicPathPrefix);
    this.quantity = product.getQuantity();
    this.pricePerItem = product.getPricePerItem();
    this.manufatured = product.getManufatured();
    this.sellBy = product.getSellBy();
    this.bestBefore = product.getBestBefore();
    this.expires = product.getExpires();
  }
}
