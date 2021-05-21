package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.CreateBusinessDto;
import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.service.BusinessService;
import com.navbara_pigeons.wasteless.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
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
   * Endpoint allowing a business admin to add a new listing to a business
   * @param listing listing to create
   * @return Returns the newly created listing id and 201 status code in a ResponseEntity
   * @throws ResponseStatusException TODO 
   */
//  @PostMapping("/businesses/{id}/listings")
//  public ResponseEntity<JSONObject> addListing(@PathVariable long id, @RequestBody CreateListingDto listing) {
//    try {
//      JSONObject listingId = listingService.addListing(id, new Listing(listing));
//      log.info("LISTING CREATED SUCCESSFULLY: " + listingId.get("listingId") + "FOR BUSINESS " + id);
//      return new ResponseEntity<>(listingId, HttpStatus.valueOf(201));
//    } catch (Exception exc) {
//      log.error("CRITICAL LISTING CREATION ERROR FOR BUSINESS " + id + " (" + exc.getMessage() + ")");
//      throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown error.");
//    }
//  }

  /**
   * Gets listings from the business with the given id
   *
   * @param id unique identifier of the business being searched for
   * @return listings from the business
   * @throws ResponseStatusException HTTP 401 Unauthorised & 406 Not Acceptable
   */
  @GetMapping("/businesses/{id}/listings")
  public ResponseEntity<Object> getBusinessById(@PathVariable long id) throws UserNotFoundException, BusinessNotFoundException {
    log.info("GETTING LISTINGS FOR BUSINESS WITH ID " + id);
    return new ResponseEntity<>(listingService.getListings(id),
        HttpStatus.valueOf(200));
  }
}