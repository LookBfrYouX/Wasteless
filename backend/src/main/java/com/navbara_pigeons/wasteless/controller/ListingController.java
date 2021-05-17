package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ForbiddenException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

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
   * Endpoint allowing a business admin to add a new listing to a business
   *
   * @param businessId Id of the business to add the listing to
   * @param listing    listing to add to the business
   * @return Returns the newly created listing id and 201 status code in a ResponseEntity
   * @throws ResponseStatusException TODO
   */
  @PostMapping("/businesses/{businessId}/listings")
  public ResponseEntity<Long> addListing(@PathVariable long businessId,
      @RequestBody Listing listing) {
    try {
      Long listingId = listingService.addListing(businessId, listing);
      log.info("LISTING CREATED SUCCESSFULLY: " + listingId + " FOR BUSINESS " + businessId);
      return new ResponseEntity<>(listingId, HttpStatus.CREATED);
    } catch (BusinessNotFoundException | ListingValidationException exc) {
      log.error(
          "INVALID DATA SUPPLIED WHEN ADDING NEW LISTING FOR BUSINESS " + businessId + " (" + exc.getMessage()
              + ")");
      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid data supplied.");
    } catch (ForbiddenException exc) {
      log.error("USER LACKS PRIVILEGES TO ADD LISTING TO BUSINESS " + businessId);
      throw new ResponseStatusException(HttpStatus.FORBIDDEN, " Invalid Privileges.");
    } catch (Exception exc) {
      log.error(
          "CRITICAL LISTING CREATION ERROR FOR BUSINESS " + businessId + " (" + exc.getMessage()
              + ")");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error.");
    }
  }
}