package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ForbiddenException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.dto.FullListingDto;
import java.util.List;

public interface ListingService {

  Long addListing(long businessId, Listing listing)
      throws ForbiddenException, BusinessNotFoundException, UserNotFoundException, ListingValidationException, InsufficientPrivilegesException;

  List<FullListingDto> getListings(long businessId) throws BusinessNotFoundException, UserNotFoundException;
}