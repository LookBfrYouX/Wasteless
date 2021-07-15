package com.navbara_pigeons.wasteless.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Data;

import javax.validation.constraints.*;

@Data
public class CreateListingDto {

  private long id;

  @NotNull(message = "Inventory Item Id cannot be Null")
  private long inventoryItemId;


  @NotNull(message = "Quantity cannot be Null")
  @Min(message = "Quantity Has To Be Positive", value = 0)
  private long quantity;


  @NotNull(message = "Price cannot be Null")
  @Min(message = "Quantity Has To Be Positive", value = 0)
  private Double price;

  @Max(message = "More Info has to be less than or equal to 50 Characters", value = 50)
  private String moreInfo;
  private LocalDate created;
  private ZonedDateTime closes;

  public CreateListingDto() {

  }
}
