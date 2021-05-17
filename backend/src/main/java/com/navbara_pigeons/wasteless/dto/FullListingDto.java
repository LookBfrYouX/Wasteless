package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Listing;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class FullListingDto {
  private long id;
  private BasicProductDto product;
  private long quantity;
  private double price;
  private String moreInfo;
  private ZonedDateTime created;
  private ZonedDateTime closes;

  public FullListingDto(Listing listing, String publicPathPrefix) {
    id = listing.getId();
    product = new BasicProductDto(listing.getInventoryItem().getProduct(), publicPathPrefix);
    quantity = listing.getQuantity();
    price = listing.getPrice();
    created = listing.getCreated();
    closes = listing.getCloses();
  }
}