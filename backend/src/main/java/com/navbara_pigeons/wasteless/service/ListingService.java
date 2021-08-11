package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.FullListingDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.enums.ListingSortByOption;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.time.LocalDate;
import java.util.List;

public interface ListingService {

  Long addListing(long businessId, long inventoryItemId, Listing listing)
      throws BusinessNotFoundException, UserNotFoundException, ListingValidationException, InsufficientPrivilegesException, InventoryItemNotFoundException;

  PaginationDto<FullListingDto> getListings(long businessId, Integer pagStartIndex,
      Integer pagEndIndex, ListingSortByOption sortBy, boolean isAscending)
      throws BusinessNotFoundException, UserNotFoundException, InvalidPaginationInputException;

  PaginationDto<FullListingDto> searchListings(Integer pagStartIndex, Integer pagEndIndex,
      ListingSortByOption sortBy, boolean isAscending, List<String> searchKey, String searchValue,
      Double minPrice, Double maxPrice, List<LocalDate> filterDates, List<BusinessType> businessTypes) throws InvalidPaginationInputException;
}