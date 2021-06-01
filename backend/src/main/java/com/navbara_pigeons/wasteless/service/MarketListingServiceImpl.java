package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.MarketListingDao;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MarketListingServiceImpl implements MarketListingService {

  private MarketListingDao marketListingDao;

  public MarketListingServiceImpl(@Autowired MarketListingDao marketListingDao) {
    this.marketListingDao = marketListingDao;
  }

  @Override
  @Transactional
  public Long saveMarketListing(MarketListing marketListing) {
    marketListing.setCreated(ZonedDateTime.now());
    marketListing.setDisplayPeriodEnd(ZonedDateTime.now().plus(1, ChronoUnit.MONTHS));
    this.marketListingDao.saveMarketListing(marketListing);
    return marketListing.getId();
  }

  @Override
  @Transactional
  public List<MarketListing> getMarketListings(String section) {
    return marketListingDao.getMarketListing(section);
  }
}
