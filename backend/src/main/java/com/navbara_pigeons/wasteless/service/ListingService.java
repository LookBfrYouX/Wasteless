package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ForbiddenException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;

public interface ListingService {

  Long addListing(long businessId, Listing listing)
      throws ForbiddenException, BusinessNotFoundException, UserNotFoundException, ListingValidationException;
}