package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.MarketListing;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MarketListingDao {

  void saveMarketListing(MarketListing marketListing);

  List<MarketListing> getMarketListing(String section);

}
