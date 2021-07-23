package com.navbara_pigeons.wasteless.dto;

import java.util.List;
import lombok.Data;

@Data
public class CreateMarketListingDto {

  private Long creatorId;
  private String section;
  private String title;
  private String description;
  private List<Long> keywordIds;
}
