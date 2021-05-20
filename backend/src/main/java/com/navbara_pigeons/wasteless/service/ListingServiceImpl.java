package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.ListingDao;
import com.navbara_pigeons.wasteless.dto.CreateListingDto;
import com.navbara_pigeons.wasteless.dto.FullListingDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.validation.ListingServiceValidation;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Class for dealing with all business logic to do with listings
 */
@Service
public class ListingServiceImpl implements ListingService {

  private final UserService userService;
  private final BusinessService businessService;
  private final ListingDao listingDao;
  private final InventoryService inventoryService;

  /**
   * ListingService constructor that takes autowired parameters and sets up the service for
   * interacting with all listing related services.
   */
  @Autowired
  public ListingServiceImpl(UserService userService, BusinessService businessService,
      ListingDao listingDao, InventoryService inventoryService) {
    this.userService = userService;
    this.businessService = businessService;
    this.listingDao = listingDao;
    this.inventoryService = inventoryService;
  }

  /**
   * Adds a given listing to a businesses listings
   *
   * @param businessId id of the business to add the listing to
   * @param listingDto listing dto of the listing to be added tot the business
   * @return newly created listing id
   * @throws InsufficientPrivilegesException  when a user is not admin nor business admin
   * @throws BusinessNotFoundException        when no business with given id exists
   * @throws UserNotFoundException            this will be caught by spring first
   */
  public Long addListing(long businessId, CreateListingDto listingDto)
      throws InsufficientPrivilegesException, BusinessNotFoundException, UserNotFoundException, ListingValidationException {
    if (!userService.isAdmin() && !businessService.isBusinessAdmin(businessId)) {
      throw new InsufficientPrivilegesException(
          "Only admins and business admins are allowed to add listings to a business");
    }
    // Add inventory item to listing from given id
    Listing listing = new Listing(listingDto);
    listing.setInventoryItem(inventoryService.getInventoryItemById(businessId, listingDto.getInventoryItemId()));
    if (listing.getCloses() == null) {
      listing.setCloses(listing.getInventoryItem().getExpires());
    }
    if (!ListingServiceValidation.isListingValid(listing)) {
      throw new ListingValidationException();
    }
    listingDao.save(listing);
    return listing.getId();
  }

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
    for (InventoryItem inventory: business.getInventory()) {
      for (Listing listing: inventory.getListings()) {
        listings.add(new FullListingDto(listing));
      }
    }

    return listings;
  }
}