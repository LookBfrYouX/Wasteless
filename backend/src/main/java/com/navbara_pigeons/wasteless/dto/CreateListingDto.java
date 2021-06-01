package com.navbara_pigeons.wasteless.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Data;

@Data
public class CreateListingDto {

  private long id;
  private long inventoryItemId;
  private long quantity;
  private Double price;
  private String moreInfo;
  private LocalDate created;
  private ZonedDateTime closes;

  public CreateListingDto() {

  }
}
