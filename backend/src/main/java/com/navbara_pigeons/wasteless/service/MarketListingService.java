package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.FullMarketListingDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.MarketListing;

public interface MarketListingService {

  Long saveMarketListing(MarketListing marketListing);

  PaginationDto<FullMarketListingDto> getMarketListings(
      String section, String sortBy, Integer pagStartIndex, Integer pagEndIndex);
}
