package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.FullMarketListingDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.enums.MarketListingSortByOption;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InvalidMarketListingSectionException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.util.List;

public interface MarketListingService {

  Long saveMarketListing(MarketListing marketListing, List<Long> keywordIds, Long creatorId)
      throws UserNotFoundException, InsufficientPrivilegesException;

  PaginationDto<FullMarketListingDto> getMarketListings(
      String section, MarketListingSortByOption sortBy, Integer pagStartIndex, Integer pagEndIndex,
      boolean isAscending)
      throws InvalidPaginationInputException, InvalidMarketListingSectionException;
}
