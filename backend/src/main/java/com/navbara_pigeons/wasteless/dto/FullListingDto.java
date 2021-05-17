package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Listing;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class FullListingDto {
  private long id;
  private FullInventoryDto inventoryItem;
  private long quantity;
  private double price;
  private String moreInfo;
  private ZonedDateTime created;
  private ZonedDateTime closes;

  public FullListingDto(Listing listing, String publicPathPrefix) {
    id = listing.getId();
    inventoryItem = new FullInventoryDto(listing.getInventory(), publicPathPrefix);
    quantity = listing.getQuantity();
    price = listing.getPrice();
    created = listing.getCreated();
    closes = listing.getCloses();
  }

  public FullListingDto() {

  }
}