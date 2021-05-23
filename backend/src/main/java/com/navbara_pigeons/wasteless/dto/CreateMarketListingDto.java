package com.navbara_pigeons.wasteless.dto;

import com.navbara_pigeons.wasteless.entity.MarketListing;
import lombok.Data;

@Data
public class CreateMarketListingDto {

    private Long creatorId;
    private String section;
    private String title;
    private String description;

}
