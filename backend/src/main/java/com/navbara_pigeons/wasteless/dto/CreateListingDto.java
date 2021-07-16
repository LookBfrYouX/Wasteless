package com.navbara_pigeons.wasteless.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;

@Data
public class CreateListingDto {

  private long id;

  @NotNull(message = "Inventory Item Id cannot be Null")
  private long inventoryItemId;


  @NotNull(message = "Quantity cannot be Null")
  @DecimalMin(inclusive = false, value = "0")
  private long quantity;


  @NotNull(message = "Price cannot be Null")
  @Min(message = "Price Has To Be Positive", value = 0)
  private Double price;

  @Length(max=50, message = "More Info has to be less than or equal to 50 Characters")
  private String moreInfo;
  private LocalDate created;
  private ZonedDateTime closes;

  public CreateListingDto() {

  }
}
