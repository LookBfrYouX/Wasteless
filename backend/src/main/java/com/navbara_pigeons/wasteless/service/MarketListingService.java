package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.entity.MarketListing;
import java.util.List;

public interface MarketListingService {

  Long saveMarketListing(MarketListing marketListing);

  List<MarketListing> getMarketListings(String section);

}
