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

import java.util.List;

@Controller
@Slf4j
@RequestMapping("")
public class MarketListingController {

    private UserService userService;
    private MarketListingService marketListingService;

    public MarketListingController(@Autowired UserService userService, MarketListingService marketListingService) {
        this.userService = userService;
        this.marketListingService = marketListingService;
    }

    @PostMapping("/cards")
    public ResponseEntity<Long> addMarketListing(@RequestBody CreateMarketListingDto createMarketListingDto) throws UserNotFoundException, UnhandledException {
        User creator = userService.getUserById(createMarketListingDto.getCreatorId());
        MarketListing marketListing = new MarketListing(createMarketListingDto, creator);
        return new ResponseEntity<>(this.marketListingService.saveMarketListing(marketListing), HttpStatus.valueOf(201));
    }

    @GetMapping("/cards")
    public ResponseEntity<List<MarketListing>> getMarketListings(@RequestParam String section) {
        return new ResponseEntity<>(this.marketListingService.getMarketListings(section), HttpStatus.OK);
    }

}
