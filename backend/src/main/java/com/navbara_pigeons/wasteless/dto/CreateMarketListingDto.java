package com.navbara_pigeons.wasteless.dto;

import java.util.List;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

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
  @Length(max=50, message = "Title has to be less than or equal to 50 Characters")
  private String title;

  @Length(max=250, message = "Description has to be less than or equal to 250 Characters")
  private String description;
  private List<Long> keywordIds;
}
