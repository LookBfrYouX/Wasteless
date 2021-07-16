package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Listing;
import java.time.ZonedDateTime;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

@Data
public class FullListingDto {

  private long id;
  private FullInventoryItemDto inventoryItem;

  @NotNull(message = "Quantity cannot be Null")
  private long quantity;

  @NotNull(message = "Price cannot be Null")
  private Double price;

  @Length(max=50, message = "More Info has to be less than or equal to 50 Characters")
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