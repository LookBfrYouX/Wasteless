package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.exception.*;
import com.navbara_pigeons.wasteless.service.ListingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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