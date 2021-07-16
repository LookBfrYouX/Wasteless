package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.MarketListing;
import java.time.ZonedDateTime;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;

@Data
public class FullMarketListingDto {

  private Long id;
  private FullUserDto creator;

  @NotBlank(message = "Section is Required")
  private String section;
  private ZonedDateTime created;
  private ZonedDateTime displayPeriodEnd;

  @NotBlank(message = "Title is Required")
  @Length(max=50, message = "Title has to be less than or equal to 50 Characters")
  private String title;

  @Length(max=250, message = "Description has to be less than or equal to 250 Characters")
  private String description;

  public FullMarketListingDto(MarketListing marketListing) {
    this.id = marketListing.getId();
    this.creator = new FullUserDto(marketListing.getCreator());
    this.section = marketListing.getSection();
    this.created = marketListing.getCreated();
    this.displayPeriodEnd = marketListing.getDisplayPeriodEnd();
    this.title = marketListing.getTitle();
    this.description = marketListing.getDescription();
  }

}
