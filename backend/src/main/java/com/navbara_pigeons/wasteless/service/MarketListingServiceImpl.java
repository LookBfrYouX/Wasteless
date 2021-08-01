package com.navbara_pigeons.wasteless.service;

import com.navbara_pigeons.wasteless.dao.MarketListingDao;
import com.navbara_pigeons.wasteless.dto.FullMarketListingDto;
import com.navbara_pigeons.wasteless.dto.PaginationDto;
import com.navbara_pigeons.wasteless.entity.MarketListing;
import com.navbara_pigeons.wasteless.entity.User;
import com.navbara_pigeons.wasteless.enums.MarketListingSortByOption;
import com.navbara_pigeons.wasteless.enums.MarketplaceSection;
import com.navbara_pigeons.wasteless.exception.InsufficientPrivilegesException;
import com.navbara_pigeons.wasteless.exception.InvalidMarketListingSectionException;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.exception.UserNotFoundException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;

@Service
public class MarketListingServiceImpl implements MarketListingService {

  private final MarketListingDao marketListingDao;
  private final UserService userService;
  private final KeywordService keywordService;

  @Autowired
  public MarketListingServiceImpl(MarketListingDao marketListingDao, UserService userService,
      KeywordService keywordService) {
    this.marketListingDao = marketListingDao;
    this.userService = userService;
    this.keywordService = keywordService;
  }

  @Override
  @Transactional
  public Long saveMarketListing(MarketListing marketListing, List<Long> keywordIds, Long creatorId)
      throws UserNotFoundException, InsufficientPrivilegesException {
    User currentUser = userService.getLoggedInUser();
    if (!userService.isAdmin() && currentUser.getId() != creatorId) {
      throw new InsufficientPrivilegesException(
          "Only a GAA can create a market listing as an other user.");
    }
    marketListing.setCreated(ZonedDateTime.now());
    marketListing.setDisplayPeriodEnd(ZonedDateTime.now().plus(1, ChronoUnit.MONTHS));
    marketListing.setCreator(userService.getUserById(creatorId));
    marketListing.setKeywords(keywordService.getKeywords(keywordIds));
    this.marketListingDao.saveMarketListing(marketListing);
    return marketListing.getId();
  }

  @Override
  @Transactional
  public PaginationDto<FullMarketListingDto> getMarketListings(String section,
      MarketListingSortByOption sortBy, Integer pagStartIndex, Integer pagEndIndex,
      boolean isAscending)
      throws InvalidPaginationInputException, InvalidMarketListingSectionException {
    try {
      MarketplaceSection.valueOf(section);
    } catch (IllegalArgumentException | NullPointerException e) {
      throw new InvalidMarketListingSectionException("Invalid section given");
    }

    PaginationBuilder pagBuilder = new PaginationBuilder(MarketListing.class, sortBy);
    pagBuilder.withPagStartIndex(pagStartIndex)
        .withPagEndIndex(pagEndIndex)
        .withSortAscending(isAscending);

    Pair<List<MarketListing>, Long> dataAndTotalCount = marketListingDao
        .getMarketListing(section, pagBuilder);

    List<FullMarketListingDto> clientResults = new ArrayList<>();
    for (MarketListing marketListing : dataAndTotalCount.getFirst()) {
      clientResults.add(new FullMarketListingDto(marketListing));
    }
    return new PaginationDto<>(clientResults, dataAndTotalCount.getSecond());
  }
}
