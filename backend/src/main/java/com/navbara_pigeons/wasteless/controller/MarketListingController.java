package com.navbara_pigeons.wasteless.controller;

import com.navbara_pigeons.wasteless.dto.CreateMarketListingDto;
import com.navbara_pigeons.wasteless.dto.FullMarketListingDto;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.entity.User;
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
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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

  @GetMapping("/cards")
  public ResponseEntity<List<FullMarketListingDto>> getMarketListings(
      @RequestParam String section) {
    log.info("GETTING CARDS FROM THE '" + section + "' SECTION");
    List<FullMarketListingDto> marketListingDtos = new ArrayList<>();
    for (MarketListing marketListing : this.marketListingService.getMarketListings(section)) {
      marketListingDtos.add(new FullMarketListingDto(marketListing));
    }
    return new ResponseEntity<>(marketListingDtos, HttpStatus.OK);
  }

}
