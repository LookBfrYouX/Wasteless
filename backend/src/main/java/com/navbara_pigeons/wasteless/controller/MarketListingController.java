package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.CreateMarketListingDto;
import com.navbara_pigeons.wasteless.dto.FullMarketListingDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.UnhandledException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.service.MarketListingService;
import com.navbara_pigeons.wasteless.service.UserService;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Slf4j
@RequestMapping("")
public class MarketListingController {

  private final UserService userService;
  private final MarketListingService marketListingService;

  public MarketListingController(@Autowired UserService userService,
      MarketListingService marketListingService) {
    this.userService = userService;
    this.marketListingService = marketListingService;
  }

  @PostMapping("/cards")
  public ResponseEntity<JSONObject> addMarketListing(
      @RequestBody CreateMarketListingDto createMarketListingDto)
      throws UserNotFoundException, UnhandledException {
    log.info("CREATING A CARD WITH TITLE: " + createMarketListingDto.getTitle());
    User creator = userService.getUserById(createMarketListingDto.getCreatorId());
    MarketListing marketListing = new MarketListing(createMarketListingDto, creator);
    JSONObject response = new JSONObject();
    response.put("cardId", this.marketListingService.saveMarketListing(marketListing));
    return new ResponseEntity<>(response, HttpStatus.valueOf(201));
  }

  /**
   * @param section
   * @param pagStartIndex The start index of the list to return, implemented for pagination, Can be
   *                      Null
   * @param pagEndIndex   The stop index of the list to return, implemented for pagination, Can be
   *                      Null
   * @param sortBy        Defines any user sorting needed and the direction (ascending or
   *                      descending). In the format "fieldName-<acs/desc>", Can be Null
   * @return List of all paginated/sorted market listings that match the section String
   */
  @GetMapping("/cards")
  public ResponseEntity<PaginationDto<FullMarketListingDto>> getMarketListings(
      @RequestParam String section,
      @RequestParam(required = false) Integer pagStartIndex,
      @RequestParam(required = false) Integer pagEndIndex,
      @RequestParam(required = false) String sortBy) throws InvalidPaginationInputException {
    log.info("GETTING CARDS FROM THE '" + section + "' SECTION");
    return new ResponseEntity<>(
        this.marketListingService.getMarketListings(section, sortBy, pagStartIndex, pagEndIndex),
        HttpStatus.OK);
  }
}
