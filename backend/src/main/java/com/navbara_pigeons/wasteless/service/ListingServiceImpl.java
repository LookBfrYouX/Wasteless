package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dto.FullListingDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Inventory;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.Product;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

//  public JSONObject addListing(long businessId, Listing listing) {
//    return null;
//    if (!userService.isAdmin() && !businessService.isBusinessAdmin(businessId)) {
//      // TODO change exception
//      return new Exception("Only admins and business admins are allowed to add listings to a business");
//    }
//  }

  /**
   * Gets all listings for the given business
   * @param businessId id of business
   * @return listings in no guaranteed order
   * @throws BusinessNotFoundException
   * @throws UserNotFoundException
   */
  public List<FullListingDto> getListings(long businessId) throws BusinessNotFoundException, UserNotFoundException {
    Business business = businessService.getBusiness(businessId);
    ArrayList<FullListingDto> listings = new ArrayList<>();
//    System.out.println(business.getInventory().size());
    for (Inventory inventory: business.getInventory()) {
//      System.out.println(inventory);
//      System.out.println(inventory.getListings().size());
      for (Listing listing: inventory.getListings()) {
        listings.add(new FullListingDto(listing, publicPathPrefix));
      }
    }

    return listings;
  }
}