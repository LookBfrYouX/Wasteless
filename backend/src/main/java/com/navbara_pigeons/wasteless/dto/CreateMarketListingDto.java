package com.navbara_pigeons.wasteless.dto;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateMarketListingDto {

  @NotNull(message = "Creator Id cannot be Null")
  private Long creatorId;

  @NotBlank(message = "Section is Required")
  private String section;

  @NotBlank(message = "Title is Required")
  @Max(message = "Title has to be less than or equal to 50 Characters", value = 50)
  private String title;

  @Max(message = "Description has to be less than or equal to 250 Characters", value = 250)
  private String description;

}
