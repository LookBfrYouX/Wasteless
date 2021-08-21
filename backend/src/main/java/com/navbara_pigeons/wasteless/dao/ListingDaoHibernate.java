package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.InvalidPaginationInputException;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;
import org.springframework.data.util.Pair;

public interface ListingDaoHibernate {

  Pair<List<Listing>, Long> getListings(Business business, PaginationBuilder pagBuilder)
      throws InvalidPaginationInputException;

  void saveListing(Listing listing);

  void deleteListing(Long listingId);

}
