package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.FullListingDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;

public class ListingServiceImpl implements ListingService {
  private final UserService userService;
  private final BusinessService businessService;

  @Value("${public_path_prefix}")
  private String publicPathPrefix;

  /**
   * ListingService constructor that takes autowired parameters and sets up the service for interacting with all listing related services.
   */
  @Autowired
  public ListingServiceImpl(UserService userService, BusinessService businessService) {
    this.userService = userService;
    this.businessService = businessService;
  }

//  public JSONObject addListing(long businessId, Listing listing) {
//    return null;
//    if (!userService.isAdmin() && !businessService.isBusinessAdmin(businessId)) {
//      // TODO change exception
//      return new Exception("Only admins and business admins are allowed to add listings to a business");
//    }
//  }

  public List<FullListingDto> getListings(long businessId) throws BusinessNotFoundException, UserNotFoundException {
    Business business = businessService.getBusiness(businessId);
    ArrayList<FullListingDto> listings = new ArrayList<>(business.getListings().size());
    for(Listing listing: business.getListings()) {
      listings.add(new FullListingDto(listing, publicPathPrefix));
    }

    return listings;
  }
}