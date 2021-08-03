package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.CreateListingDto;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.enums.ListingSortByOption;
import com.navbara_pigeons.wasteless.exception.BusinessNotFoundException;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.InventoryItemNotFoundException;
import com.navbara_pigeons.wasteless.exception.ListingValidationException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.service.ListingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * This controller class provides the endpoints for dealing with business listings
 */
@RestController
@Slf4j
@Validated
@RequestMapping("")
@Tag(name = "Listing Endpoint", description = "The API endpoint for Product Listing related requests")
public class ListingController {

  private final ListingService listingService;

  @Autowired
  public ListingController(ListingService listingService) {
    this.listingService = listingService;
  }


  @GetMapping("/listings/search")
  @Operation(summary = "Search through sales listings", description = "Search and filter all sales listings")
  public ResponseEntity<Object> searchListings(
          @Parameter(description = "Pagination start index") @RequestParam(required = false) @Min(0) Integer pagStartIndex,
          @Parameter(description = "Pagination end index") @RequestParam(required = false) @Min(0) Integer pagEndIndex,
          @Parameter(description = "Sort option") @RequestParam(required = false) ListingSortByOption sortBy,
          @Parameter(description = "Search key") @RequestParam(required = false) String searchKey,
          @Parameter(description = "Search value") @RequestParam(required = false) String searchParam
  ) {
    log.info("GETTING LISTINGS FOR: " + searchKey + " = " + searchParam);
    // TODO connect to service method to get result list
    return new ResponseEntity<>("Success", HttpStatus.OK);
  }

  /**
   * Adds an inventory item to a businesses inventory
   *
   * @param businessId Id of the business to add the listing to
   * @param listingDto listing to add to the business
   * @return Returns the newly created listing id and 201 status code in a ResponseEntity
   */
  @PostMapping("/businesses/{businessId}/listings")
  public ResponseEntity<JSONObject> addListing(
      @PathVariable long businessId, @RequestBody @Valid CreateListingDto listingDto)
      throws UserNotFoundException, BusinessNotFoundException, InsufficientPrivilegesException,
      ListingValidationException, InventoryItemNotFoundException {

    Long listingId =
        listingService.addListing(
            businessId, listingDto.getInventoryItemId(), new Listing(listingDto));
    log.info("LISTING CREATED SUCCESSFULLY: " + listingId + " FOR BUSINESS " + businessId);
    JSONObject response = new JSONObject();
    response.appendField("listingId", listingId);
    return new ResponseEntity<>(response, HttpStatus.CREATED);
  }

  /**
   * This controller endpoint is used to retrieve businesses listings from a specified business.
   *
   * @param id            The business ID
   * @param pagStartIndex The start index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param pagEndIndex   The stop index of the list to return, implemented for pagination, Can be
   *                      Null. This index is inclusive.
   * @param sortBy        Defines the field to be sorted, can be null and defaults to the 'id'
   *                      field.
   * @param isAscending   Boolean value, whether the sort order should be in ascending order. Is not
   *                      required and defaults to True.
   * @return A ResponseEntity with a list of listings.
   * @throws UserNotFoundException     Handled in ControllerExceptionHandler class.
   * @throws BusinessNotFoundException Handled in ControllerExceptionHandler class.
   */
  @GetMapping("/businesses/{id}/listings")
  @Operation(summary = "Show a businesses listings", description = "Return a paginated/sorted list of a specific businesses listings")
  public ResponseEntity<Object> showBusinessListings(
      @Parameter(
          description = "The unique ID number of the business"
      ) @PathVariable long id,
      @Parameter(
          description = "The start index of the list to return, implemented for pagination, Can be "
              + "Null. This index is inclusive."
      ) @RequestParam(required = false) Integer pagStartIndex,
      @Parameter(
          description = "The stop index of the list to return, implemented for pagination, Can be "
              + "Null. This index is inclusive."
      ) @RequestParam(required = false) Integer pagEndIndex,
      @Parameter(
          description = "Defines the field to be sorted, can be null."
      ) @RequestParam(required = false) ListingSortByOption sortBy,
      @Parameter(
          description = "Boolean value, whether the sort order should be in ascending order. Is not"
              + " required and defaults to True."
      ) @RequestParam(required = false, defaultValue = "true") boolean isAscending)
      throws UserNotFoundException, BusinessNotFoundException, InvalidPaginationInputException {
    log.info("GETTING LISTINGS FOR BUSINESS WITH ID " + id);
    return new ResponseEntity<>(
        listingService.getListings(id, pagStartIndex, pagEndIndex, sortBy, isAscending),
        HttpStatus.OK);
  }
}
