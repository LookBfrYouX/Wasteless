package com.navbara_pigeons.wasteless.dao;

import com.navbara_pigeons.wasteless.entity.Listing;
import com.navbara_pigeons.wasteless.exception.ListingNotFoundException;

public interface ListingDao {

  void saveListing(Listing listing);
}
