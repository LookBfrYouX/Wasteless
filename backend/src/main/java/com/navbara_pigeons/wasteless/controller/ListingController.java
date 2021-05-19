package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.CreateListingDto;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.ForbiddenException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import com.navbara_pigeons.wasteless.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
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
   *
   * @param businessId Id of the business to add the listing to
   * @param listing    listing to add to the business
   * @return Returns the newly created listing id and 201 status code in a ResponseEntity
   * @throws ResponseStatusException with 400 and 403 responses
   */
  @PostMapping("/businesses/{businessId}/listings")
  public ResponseEntity<Long> addListing(@PathVariable long businessId,
      @RequestBody CreateListingDto listing) {
    try {
      Long listingId = listingService.addListing(businessId, listing);
      log.info("LISTING CREATED SUCCESSFULLY: " + listingId + " FOR BUSINESS " + businessId);
      return new ResponseEntity<>(listingId, HttpStatus.CREATED);
    } catch (BusinessNotFoundException | ListingValidationException exc) {
      log.error(
          "INVALID DATA SUPPLIED WHEN ADDING NEW LISTING FOR BUSINESS " + businessId + " (" + exc
              .getMessage()
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

  /**
   * Gets listings from the business with the given id
   *
   * @param id unique identifier of the business being searched for
   * @return listings from the business
   * @throws ResponseStatusException HTTP 401 Unauthorised & 406 Not Acceptable
   */
  @GetMapping("/businesses/{id}/listings")
  public ResponseEntity<Object> getBusinessById(@PathVariable long id) {
    try {
      log.info("GETTING LISTINGS FOR BUSINESS WITH ID " + id);
      return new ResponseEntity<>(listingService.getListings(id),
          HttpStatus.valueOf(200));
    } catch (BusinessNotFoundException exc) {
      log.error("GETTING LISTINGS, BUSINESS NOT FOUND ERROR: " + id);
      throw new ResponseStatusException(HttpStatus.valueOf(406), exc.getMessage());
    } catch (UserNotFoundException exc) {
      log.error("GETTING LISTINGS, USER NOT FOUND ERROR: " + id);
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exc.getMessage());
    } catch (Exception exc) {
      log.error(
          "CRITICAL ERROR GETTING LISTINGS FOR BUSINESS " + id + " (" + exc.getMessage() + ")");
      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, exc.getMessage());
    }
  }
}