package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.Listing;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class CreateListingDto {
  private long id;
  private long inventoryItemId;
  private long quantity;
  private float price;
  private String moreInfo;
  private LocalDate created;
  private LocalDate closes;

  public CreateListingDto() {

  }
}
