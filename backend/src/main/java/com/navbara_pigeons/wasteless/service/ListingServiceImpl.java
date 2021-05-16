package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.FullListingDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Class for dealing with all business logic to do with listings
 */
@Service
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

  /**
   * Adds a given listing to a businesses listings
   *
   * @param businessId id of the business to add the listing to
   * @param listing listing to be added tot the business
   * @return newly created listing id
   * @throws Exception this will be changed in due time
   */
  public JSONObject addListing(long businessId, Listing listing) throws Exception {
    return new JSONObject();
    if (!userService.isAdmin() && !businessService.isBusinessAdmin(businessId)) {
      // TODO change exception
      throw new Exception("Only admins and business admins are allowed to add listings to a business");
    }
  }

  public List<FullListingDto> getListings(long businessId) throws BusinessNotFoundException, UserNotFoundException {
    Business business = businessService.getBusiness(businessId);
    ArrayList<FullListingDto> listings = new ArrayList<>(business.getListings().size());
    for(Listing listing: business.getListings()) {
      listings.add(new FullListingDto(listing, publicPathPrefix));
    }

    return listings;
  }
}