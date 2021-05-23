package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.MarketListingDao;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
public class MarketListingServiceImpl implements MarketListingService {

    private MarketListingDao marketListingDao;

    public MarketListingServiceImpl(@Autowired MarketListingDao marketListingDao) {
        this.marketListingDao = marketListingDao;
    }

    @Override
    public Long saveMarketListing(MarketListing marketListing) {
        marketListing.setCreated(ZonedDateTime.now());
        marketListing.setDisplayPeriodEnd(ZonedDateTime.now().plus(1, ChronoUnit.MONTHS));
        this.marketListingDao.save(marketListing);
        return marketListing.getId();
    }

    @Override
    public List<MarketListing> getMarketListings(String section) {
        return new ArrayList<>();
        // return this.marketListingDao.; Create DAO first
    }
}
