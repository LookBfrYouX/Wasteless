package com.navbara_pigeons.wasteless.dto;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
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
  @DecimalMin(message="price must be above 0.01", value="0.01")
  @DecimalMax(message="price must be below 10,000,000", value="10000000.00")
  private Double price;

  @Length(max=50, message = "More Info has to be less than or equal to 50 Characters")
  private String moreInfo;
  private LocalDate created;
  private ZonedDateTime closes;

  public CreateListingDto() {

  }
}
