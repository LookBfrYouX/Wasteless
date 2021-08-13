package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.BusinessType;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.util.Pair;

public interface ListingDao {

  Pair<List<Listing>, Long> getListings(Business business, PaginationBuilder pagBuilder)
      throws InvalidPaginationInputException;

  void saveListing(Listing listing);

  Pair<List<Listing>, Long> searchAllListings(List<String> searchKey, String searchValue,
      Double minPrice, Double maxPrice, List<LocalDate> filterDates,
      List<BusinessType> businessTypes, PaginationBuilder pagBuilder);
}
