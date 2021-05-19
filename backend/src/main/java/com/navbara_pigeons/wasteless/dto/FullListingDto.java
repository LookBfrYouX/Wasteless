package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Listing;
import java.time.LocalDate;
import lombok.Data;
import java.time.ZonedDateTime;

@Data
public class FullListingDto {
  private long id;
  private FullInventoryItemDto inventoryItem;
  private long quantity;
  private double price;
  private String moreInfo;
  private LocalDate created;
  private LocalDate closes;

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