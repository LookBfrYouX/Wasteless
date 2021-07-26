package com.navbara_pigeons.wasteless.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import lombok.Data;

@Data
public class CreateListingDto {

  private long id;
  private long inventoryItemId;
  private long quantity;
  @DecimalMin(message="price must be above 0.01", value="0.01")
  @DecimalMax(message="price must be below 10,000,000", value="10000000.00")
  private Double price;
  private String moreInfo;
  private LocalDate created;
  private ZonedDateTime closes;

  public CreateListingDto() {

  }
}
