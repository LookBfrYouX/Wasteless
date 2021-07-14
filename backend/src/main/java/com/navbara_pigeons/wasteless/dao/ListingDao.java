package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Business;
import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.helper.PaginationBuilder;
import java.util.List;
import org.springframework.data.util.Pair;

public interface ListingDao {

  Pair<List<Listing>, Long> getListings(Business business, PaginationBuilder pagBuilder);

  void saveListing(Listing listing);
}
