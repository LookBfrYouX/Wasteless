package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.CreateListingDto;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * This controller class provides the endpoints for dealing with business listings
 */
@Controller
@Slf4j
@RequestMapping("")
public class ListingController {

  private final ListingService listingService;

  @Autowired
  public ListingController(ListingService listingService) {
    this.listingService = listingService;
  }

  /**
   * Adds an inventory item to a businesses inventory
   *
   * @param businessId Id of the business to add the listing to
   * @param listingDto    listing to add to the business
   * @return Returns the newly created listing id and 201 status code in a ResponseEntity
   */
  @PostMapping("/businesses/{businessId}/listings")
  public ResponseEntity<Long> addListing(@PathVariable long businessId,
      @RequestBody CreateListingDto listingDto)
      throws UserNotFoundException, BusinessNotFoundException, InsufficientPrivilegesException, ListingValidationException, InventoryItemNotFoundException {

    Long listingId = listingService.addListing(businessId, listingDto.getInventoryItemId(), new Listing(listingDto));
    log.info("LISTING CREATED SUCCESSFULLY: " + listingId + " FOR BUSINESS " + businessId);
    return new ResponseEntity<>(listingId, HttpStatus.CREATED);
  }

  /**
   * This controller endpoint is used to retrieve businesses listings from a specified business.
   * @param id The business ID
   * @return A ResponseEntity with a list of listings.
   * @throws UserNotFoundException Handled in ControllerExceptionHandler class.
   * @throws BusinessNotFoundException Handled in ControllerExceptionHandler class.
   */
  @GetMapping("/businesses/{id}/listings")
  public ResponseEntity<Object> getBusinessById(@PathVariable long id) throws UserNotFoundException, BusinessNotFoundException {
    log.info("GETTING LISTINGS FOR BUSINESS WITH ID " + id);
    return new ResponseEntity<>(listingService.getListings(id),
        HttpStatus.valueOf(200));
  }
}