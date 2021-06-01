package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.FullListingDto;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import java.util.List;

public interface ListingService {

  Long addListing(long businessId, long inventoryItemId, Listing listing)
      throws BusinessNotFoundException, UserNotFoundException, ListingValidationException, InsufficientPrivilegesException, InventoryItemNotFoundException;

  List<FullListingDto> getListings(long businessId)
      throws BusinessNotFoundException, UserNotFoundException;
}