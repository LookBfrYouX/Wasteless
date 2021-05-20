package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.CreateListingDto;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.dto.FullListingDto;
import java.util.List;

public interface ListingService {

  Long addListing(long businessId, CreateListingDto listing)
      throws BusinessNotFoundException, UserNotFoundException, ListingValidationException, InsufficientPrivilegesException;

  List<FullListingDto> getListings(long businessId) throws BusinessNotFoundException, UserNotFoundException;
}