package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Listing;
import java.time.ZonedDateTime;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class FullListingDto {

  private long id;
  private FullInventoryItemDto inventoryItem;
  private long quantity;
  @DecimalMin(message="price must be above 0.01", value="0.01")
  @DecimalMax(message="price must be below 10,000,000", value="10000000.00")
  private Double price;
  private String moreInfo;
  private ZonedDateTime created;
  private ZonedDateTime closes;

  public FullListingDto(Listing listing, String publicPathPrefix) {
    id = listing.getId();
    inventoryItem = new FullInventoryItemDto(listing.getInventoryItem(), publicPathPrefix);
    quantity = listing.getQuantity();
    price = listing.getPrice();
    moreInfo = listing.getMoreInfo();
    created = listing.getCreated();
    closes = listing.getCloses();
    moreInfo = listing.getMoreInfo();
  }

  public FullListingDto() {

  }
}