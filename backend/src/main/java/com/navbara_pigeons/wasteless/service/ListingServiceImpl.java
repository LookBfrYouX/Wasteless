package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.ListingDao;
import com.navbara_pigeons.wasteless.dto.BasicUserDto;
import com.navbara_pigeons.wasteless.dto.FullListingDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.entity.InventoryItem;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.enums.ListingSortByOption;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import com.navbara_pigeons.wasteless.validation.ListingServiceValidation;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.util.Pair;
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
  @Value("${public_path_prefix}")
  private String publicPathPrefix;

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
   * @param listing    listing dto of the listing to be added tot the business
   * @return newly created listing id
   * @throws InsufficientPrivilegesException when a user is not admin nor business admin
   * @throws BusinessNotFoundException       when no business with given id exists
   * @throws UserNotFoundException           this will be caught by spring first
   */
  @Override
  @Transactional
  public Long addListing(long businessId, long inventoryItemId, Listing listing)
      throws InsufficientPrivilegesException, BusinessNotFoundException, UserNotFoundException, ListingValidationException, InventoryItemNotFoundException {
    if (!userService.isAdmin() && !businessService.isBusinessAdmin(businessId)) {
      throw new InsufficientPrivilegesException(
          "Only admins and business admins are allowed to add listings to a business");
    }
    // Add inventory item to listing from given id
    listing.setInventoryItem(inventoryService.getInventoryItemById(businessId, inventoryItemId));

    if (listing.getCloses() == null) {
      listing.setCloses(ZonedDateTime.of(listing.getInventoryItem().getExpires(), LocalTime.now(),
          ZoneId.systemDefault()));
    }
    // Throws exception when validation does not pass
    ListingServiceValidation.isListingValid(listing);

    listing.setCreated(ZonedDateTime.now(ZoneOffset.UTC));
    listingDao.saveListing(listing);
    return listing.getId();
  }

  /**
   * Gets all listings for the given business
   *
   * @param businessId    id of business
   * @param pagStartIndex The start index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param pagEndIndex   The stop index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param sortBy        Defines the field to be sorted, can be null.
   * @param isAscending   Boolean value, whether the sort order should be in ascending order. Is not
   *                      required and defaults to True.
   * @return listings in no guaranteed order
   * @throws BusinessNotFoundException
   * @throws UserNotFoundException
   */
  @Override
  public PaginationDto<FullListingDto> getListings(long businessId, Integer pagStartIndex,
      Integer pagEndIndex, ListingSortByOption sortBy, boolean isAscending)
      throws BusinessNotFoundException, UserNotFoundException, InvalidPaginationInputException {
    Business business = businessService.getBusiness(businessId);

    PaginationBuilder pagBuilder = new PaginationBuilder(Listing.class, sortBy);
    pagBuilder.withPagStartIndex(pagStartIndex)
        .withPagEndIndex(pagEndIndex)
        .withSortAscending(isAscending);

    Pair<List<Listing>, Long> dataAndTotalCount = listingDao.getListings(business, pagBuilder);

    ArrayList<FullListingDto> listings = new ArrayList<>();
    for (Listing listing : dataAndTotalCount.getFirst()) {
      listings.add(new FullListingDto(listing, publicPathPrefix));
    }

    return new PaginationDto<>(listings, dataAndTotalCount.getSecond());
  }

  /**
   * Get all sales listings from every businesses
   * @param pagStartIndex The start index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param pagEndIndex   The stop index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param sortBy        Defines the field to be sorted, can be null.
   * @param isAscending   Boolean value, whether the sort order should be in ascending order. Is not
   *                      required and defaults to True.
   * @return All sales listings
   */
  @Override
  public PaginationDto<FullListingDto> searchListings(Integer pagStartIndex, Integer pagEndIndex,
      ListingSortByOption sortBy, boolean isAscending, List<String> searchKey, String searchValue,
      Double minPrice, Double maxPrice, List<LocalDate> filterDates, List<BusinessType> businessTypes) throws InvalidPaginationInputException {


    PaginationBuilder pagBuilder = new PaginationBuilder(Listing.class, sortBy);
    pagBuilder.withPagStartIndex(pagStartIndex)
        .withPagEndIndex(pagEndIndex)
        .withSortAscending(isAscending);

    //TODO Comment out when ListingDao is integrated.
//    Pair<List<Listing>, Long> dataAndTotalCount = listingDao
//        .searchAllListings(searchKey, searchValue);

//    List<FullListingDto> listingResults = new ArrayList<>();
//    for (Listing listing : dataAndTotalCount.getFirst()) {
//      listingResults.add(new FullListingDto(listing, publicPathPrefix));
//    }
//    return new PaginationDto<>(listingResults, dataAndTotalCount.getSecond());
    return null;
  }
}