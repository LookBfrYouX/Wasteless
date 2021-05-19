package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Listing;
import lombok.Data;

import java.time.ZonedDateTime;
import org.springframework.beans.factory.annotation.Value;

@Data
public class FullListingDto {
  private long id;
  private FullInventoryItemDto inventoryItem;
  private long quantity;
  private double price;
  private String moreInfo;
  private ZonedDateTime created;
  private ZonedDateTime closes;

  public FullListingDto(Listing listing) {
    id = listing.getId();
    inventoryItem = new FullInventoryItemDto(listing.getInventoryItem());
    quantity = listing.getQuantity();
    price = listing.getPrice();
    created = listing.getCreated();
    closes = listing.getCloses();
  }

  public FullListingDto() {

  }
}