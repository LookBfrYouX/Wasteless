package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.FullMarketListingDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.enums.MarketListingSortByOption;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;

public interface MarketListingService {

  Long saveMarketListing(MarketListing marketListing);

  PaginationDto<FullMarketListingDto> getMarketListings(
      String section, MarketListingSortByOption sortBy, Integer pagStartIndex, Integer pagEndIndex,
      boolean isAscending)
      throws InvalidPaginationInputException;
}
