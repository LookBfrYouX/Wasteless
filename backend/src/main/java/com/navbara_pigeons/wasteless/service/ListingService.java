package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.FullListingDto;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import net.minidev.json.JSONObject;

import java.util.List;

public interface ListingService {
  JSONObject addListing(long businessId, Listing listing) throws Exception;
  List<FullListingDto> getListings(long businessId) throws BusinessNotFoundException, UserNotFoundException;
}