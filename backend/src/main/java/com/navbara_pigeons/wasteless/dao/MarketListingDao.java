package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;
import org.springframework.data.util.Pair;

public interface MarketListingDao {

  void saveMarketListing(MarketListing marketListing);

  Pair<List<MarketListing>, Long> getMarketListing(String section, PaginationBuilder pagBuilder)
      throws InvalidPaginationInputException;
}
